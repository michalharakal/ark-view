/*
 * SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import 'dart:convert';
import 'dart:isolate';

import 'package:arc_view/src/charts/models/metrics.dart';
import 'package:arc_view/src/conversation/services/conversation_colors.dart';
import 'package:arc_view/src/events/models/agent_events.dart';
import 'package:arc_view/src/events/notifiers/agent_events_notifier.dart';
import "package:collection/collection.dart";
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'agent_metrics_notifier.g.dart';

@riverpod
class AgentMetricsNotifier extends _$AgentMetricsNotifier {
  @override
  Future<List<Metrics>> build() async {
    final events = ref.watch(agentEventsNotifierProvider).toList();

    final result = await Isolate.run(() {
      return _build(events);
    });
    return result;
  }

  static Map<PlotType, Plot> _transformEvent(
      String type, dynamic json, int index) {
    return switch (type) {
      'AgentFinishedEvent' => {
          PlotType.agentDuration:
              Plot(x: index.toDouble(), y: json['duration'].toDouble()),
          PlotType.agentBreaks:
              Plot(x: index.toDouble(), y: json['flowBreak'] ? 1.0 : 0.0),
        },
      'LLMFinishedEvent' => {
          PlotType.llmTotalTokens:
              Plot(x: index.toDouble(), y: json['totalTokens'].toDouble()),
          PlotType.llmFunctionCalls: Plot(
              x: index.toDouble(), y: json['functionCallCount'].toDouble()),
          PlotType.llmPromptTokens:
              Plot(x: index.toDouble(), y: json['promptTokens'].toDouble()),
          PlotType.llmCompletionTokens:
              Plot(x: index.toDouble(), y: json['completionTokens'].toDouble()),
          PlotType.llmDuration:
              Plot(x: index.toDouble(), y: json['duration'].toDouble()),
        },
      _ => {},
    };
  }

  static List<Metrics> _build(List<AgentEvent> events) {
    Map<String, List<AgentEvent>> groupedEvents =
        groupBy(events, (e) => e.conversationId ?? '');

    return groupedEvents.keys.map((key) {
      final conversationId = key.toString();
      final events = groupedEvents[key]!;
      Map<PlotType, List<Plot>> allPlots = {};

      for (var i = 0; i < events.length - 1; i++) {
        final event = events[i];
        final plots = _transformEvent(
            event.type, jsonDecode(event.payload), events.length - i);
        for (var entry in plots.entries) {
          allPlots[entry.key] =
              (allPlots[entry.key]?..add(entry.value)) ?? [entry.value];
        }
      }
      return Metrics(
        name: conversationId,
        conversationId: conversationId,
        color: color(conversationId),
        plots: allPlots,
      );
    }).toList();
  }
}
