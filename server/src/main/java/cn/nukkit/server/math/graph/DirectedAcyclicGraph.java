/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.math.graph;

import com.google.common.collect.ImmutableList;

import java.util.*;

/**
 * Voxelwind
 * https://github.com/voxelwind/voxelwind
 * Licensed under MIT
 * https://github.com/voxelwind/voxelwind/blob/master/LICENSE.md
 * @param <T>
 */
public class DirectedAcyclicGraph<T> {
    private final List<Node<T>> nodes = new ArrayList<>();

    public static <T> List<T> sort(DirectedAcyclicGraph<T> graph) throws CycleException {
        // Now find nodes that have no edges.
        Queue<DirectedAcyclicGraph.Node<T>> noEdges = graph.getNodesWithNoEdges();

        // Run Kahn's algorithm.
        List<T> sorted = new ArrayList<>();
        while (!noEdges.isEmpty()) {
            DirectedAcyclicGraph.Node<T> itemNode = noEdges.poll();
            T item = itemNode.getData();
            sorted.add(item);

            for (DirectedAcyclicGraph.Node<T> node : graph.withEdge(item)) {
                node.removeEdge(itemNode);
                if (node.getAdjacent().isEmpty()) {
                    if (!noEdges.contains(node)) {
                        noEdges.add(node);
                    }
                }
            }
        }

        if (graph.hasEdges()) {
            throw new CycleException("Cycle found: " + graph.toString());
        }

        return sorted;
    }

    public void addEdges(T one, T two) {
        Node<T> oneNode = add(one);
        Node<T> twoNode = add(two);
        oneNode.addEdge(twoNode);
    }

    public Node<T> add(T t) {
        Optional<Node<T>> willAdd = get(t);
        if (!willAdd.isPresent()) {
            Node<T> node = new Node<>(t);
            nodes.add(node);
            return node;
        } else {
            return willAdd.get();
        }
    }

    public Optional<Node<T>> get(T t) {
        for (Node<T> node : nodes) {
            if (node.data.equals(t)) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    public List<Node<T>> withEdge(T t) {
        Optional<Node<T>> inOptional = get(t);
        if (!inOptional.isPresent()) {
            return ImmutableList.of();
        }
        Node<T> in = inOptional.get();
        List<Node<T>> list = new ArrayList<>();
        for (Node<T> node : nodes) {
            if (node.isAdjacent(in)) {
                list.add(node);
            }
        }
        return list;
    }

    public void remove(Node<T> node) {
        for (Node<T> tNode : nodes) {
            tNode.removeEdge(node);
        }
        nodes.remove(node);
    }

    public boolean hasEdges() {
        for (Node<T> node : nodes) {
            if (!node.adjacent.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "DirectedAcyclicGraph{" +
                "nodes=" + nodes +
                '}';
    }

    public Queue<Node<T>> getNodesWithNoEdges() {
        Queue<Node<T>> found = new ArrayDeque<>();
        for (Node<T> node : nodes) {
            if (node.getAdjacent().isEmpty()) {
                found.add(node);
            }
        }
        return found;
    }

    public static class Node<T> {
        private final T data;
        private final List<Node<T>> adjacent = new ArrayList<>();

        private Node(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public List<Node<T>> getAdjacent() {
            return adjacent;
        }

        public void addEdge(Node<T> edge) {
            if (!isAdjacent(edge)) {
                adjacent.add(edge);
            }
        }

        public void removeEdge(Node<T> edge) {
            adjacent.remove(edge);
        }

        public boolean isAdjacent(Node<T> edge) {
            return adjacent.contains(edge);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(data, node.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    ", adjacent=" + adjacent +
                    '}';
        }
    }
}
