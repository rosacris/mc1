package org.cifasis.mc1;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Cristian on 16/07/15.
 */
public class EventStructure {

    /* Set of events in the structure */
    private final Map<String, Event> eventSet;

    /* Initial root event */
    private Event root;

    public EventStructure() {
        this.eventSet = Maps.newHashMap();
        this.root = new Event("0");
    }

    public static boolean isConf(Set<Event> events) {
        for (Event e : events) {
            if (!events.containsAll(e.getCone()))
                return false;

            if (!Sets.intersection(e.getDirectConflicts(), events).isEmpty())
                return false;
        }
        return true;
    }

    public Set<Event> getExtensions(Set<Event> conf) {
        Set<Event> extensions = Sets.newTreeSet();

        if (!EventStructure.isConf(conf))
            throw new RuntimeException("The argument is not a valid configuration.");

        for (final Event e : this.eventSet.values()) {
            if (!conf.contains(e) && conf.containsAll(e.getCone())) {
                extensions.add(e);
            }
        }
        return extensions;
    }

    public Set<Event> getEnabled(Set<Event> conf) {
        Set<Event> enabledEvents = Sets.newTreeSet();

        if (!EventStructure.isConf(conf))
            throw new RuntimeException("The argument is not a valid configuration.");

        for (final Event e : this.getExtensions(conf)) {
            if (!conf.stream().anyMatch(new Predicate<Event>() {
                public boolean test(Event event) {
                    return e.isInConflict(event);
                }
            })) {
                enabledEvents.add(e);
            }
        }
        return enabledEvents;
    }

    public boolean isMaximalConf(Set<Event> events) {
        return isConf(events) && getEnabled(events).isEmpty();
    }

    public static Set<Event> getMaximalEvents(final Set<Event> conf) {
        if (!EventStructure.isConf(conf))
            throw new RuntimeException("The argument is not a valid configuration.");

        return conf.stream().filter(new Predicate<Event>() {
            public boolean test(final Event maximalEvent) {
                return maximalEvent.getChilds().stream().allMatch(new Predicate<Event>() {
                    public boolean test(Event childEvent) {
                        return !conf.contains(childEvent);
                    }
                });
            }
        }).collect(Collectors.<Event>toSet());
    }

    public Event newEvent(String name) {
        if (eventSet.containsKey(name)) {
            throw new RuntimeException("Event already in structure.");
        } else {
            Event newEvent = new Event(name);
            eventSet.put(name, newEvent);
            return newEvent;
        }
    }

    public Event getRoot() {
        return root;
    }

    public Set<Event> getEventSet() {
        return Sets.newTreeSet(eventSet.values());
    }

    public Event getEventByName(String name) {
        return eventSet.get(name);
    }

    @Override
    public String toString() {
        return "< = " + root.getDepsAsString(Sets.<Event>newHashSet()) + " # = " + root.getConflicts();
    }

    public static class Event implements Comparable<Event> {
        private final String name;
        private final List<Event> parents;
        private final List<Event> childs;
        private final Set<Event> conflicts;

        public Event(String name) {
            this.name = name;
            this.parents = Lists.newArrayList();
            this.childs = Lists.newArrayList();
            this.conflicts = Sets.newHashSet();
        }

        public String getName() {
            return name;
        }

        public boolean isDependent(final Event that) {
            return parents.contains(that) || parents.stream().anyMatch(new Predicate<Event>() {
                public boolean test(Event event) {
                    return event.isDependent(that);
                }
            });
        }

        public boolean isInDirectConflict(Event that) {
            return conflicts.contains(that);
        }

        public boolean isInConflict(final Event that) {
            return isInDirectConflict(that) || parents.stream().anyMatch(new Predicate<Event>() {
                public boolean test(Event event) {
                    return event.isInConflict(that);
                }
            });
        }

        public Event dependsOn(Event that) {
            if (!that.isDependent(this) && !this.isInConflict(that)) {
                that.childs.add(this);
                this.parents.add(that);
                return this;
            } else {
                throw new RuntimeException("Attempt to create a cyclic dependence.");
            }
        }

        public Event conflicts(Event that) {
            this.conflicts.add(that);
            that.conflicts.add(this);
            return this;
        }

        public Set<Event> getDirectConflicts() {
            return conflicts;
        }

        public Set<Conflict<Event>> getConflicts() {
            Set<Conflict<Event>> conflictPairs = Sets.newHashSet();
            for (Event e : conflicts) {
                conflictPairs.add(new Conflict(this, e));
            }

            for (Event e : childs) {
                conflictPairs.addAll(e.getConflicts());
            }

            return conflictPairs;
        }

        public Set<Event> getCone() {
            Set<Event> cone = Sets.newHashSet();
            for (Event e : parents) {
                cone.add(e);
                cone.addAll(e.getCone());
            }
            return cone;
        }

        public List<Event> getChilds() {
            return childs;
        }

        public String getDepsAsString(Set<Event> visited) {

            String deps = "";
            for (Event child : this.getChilds())
                deps += "(" + this.getName() + "," + child + "), ";

            visited.add(this);

            String childDeps = "";
            for (Event e : childs) {
                if(!visited.contains(e))
                childDeps += e.getDepsAsString(visited);
            }

            return deps + childDeps;
        }

        public String getDepsAsDot(Set<Event> visited){
            String deps = "";
            for (Event child : this.getChilds())
                deps += this.getName() + " -> " + child + "\n";

            visited.add(this);

            String childDeps = "";
            for (Event e : childs) {
                if(!visited.contains(e))
                    childDeps += e.getDepsAsDot(visited);
            }

            return deps + childDeps;
        }

        public String getConflictAsDot(){
            String conf = "";
            for (Conflict<Event> conflict : this.getConflicts())
                conf += conflict.left + " -> " + conflict.right + "\n";

            return conf;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Event)) return false;
            Event event = (Event) o;
            return Objects.equal(name, event.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        public int compareTo(Event o) {
            return Integer.valueOf(name).compareTo(Integer.valueOf(o.name));
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Conflict<T> {
        private final Set<T> conflict;
        private final T left;
        private final T right;

        public Conflict(T left, T right) {
            this.conflict = Sets.newHashSet(left, right);
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Conflict)) return false;
            Conflict<?> conflict1 = (Conflict<?>) o;
            return Objects.equal(conflict, conflict1.conflict);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(conflict);
        }

        @Override
        public String toString() {
            return "{" + left + "," + right + "}";
        }
    }
}
