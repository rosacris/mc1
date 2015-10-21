package org.cifasis.mc1;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Cristian on 16/07/15.
 */
public class EventStructure {

    private final Map<String, Event> eventSet;          /* Set of events in the structure */
    private Event root;                                 /* Initial root event */

    private static final Comparator<Map.Entry<Event, Event>> TUPPLE_ORDER =
            (o1, o2) -> {
                int a = o1.getKey().compareTo(o2.getKey());
                if (a != 0)
                    return a;
                else
                    return o1.getValue().compareTo(o2.getValue());
            };

    private static final Supplier<TreeSet<Map.Entry<Event, Event>>> TREE_SET_SUPPLIER =
            () -> new TreeSet<>(TUPPLE_ORDER);

    private static final PrettyPrinter<Map.Entry<Event,Event>> BASE_PRINTER = new PrettyPrinter<Map.Entry<Event, Event>>() {
        @Override
        public String print(Map.Entry<Event, Event> tupple) {
            return "(" + tupple.getKey() + "," + tupple.getValue() + ")";
        }
    };

    private static final PrettyPrinter<Map.Entry<Event,Event>> DOT_PRINTER = new PrettyPrinter<Map.Entry<Event, Event>>() {
        @Override
        public String print(Map.Entry<Event, Event> tupple) {
            return  tupple.getKey() + "->" + tupple.getValue() + "\n";
        }
    };

    public EventStructure() {
        this.eventSet = Maps.newHashMap();
        this.root = new Event(0);
        eventSet.put("0", this.root);
    }

    public static boolean isConf(Set<Event> events) {
//        for (Event e : events) {
//            if (!events.containsAll(e.getCone()))
//                return false;
//
//            if (!Sets.intersection(e.getDirectConflicts(), events).isEmpty())
//                return false;
//        }
        return true;
    }

    public Set<Event> getExtensions(Set<Event> conf) {
        Set<Event> extensions = Sets.newTreeSet();

        if (!EventStructure.isConf(conf))
            throw new RuntimeException("The argument is not a valid configuration.");

        for(final Event e: conf) {
            for(final Event child: e.getChilds()){
                if(!conf.contains(child) && conf.containsAll(child.getParents()))
                    extensions.add(child);
            }
        }

        return extensions;
    }

    public Set<Event> getEnabled(Set<Event> conf) {
        Set<Event> enabledEvents = Sets.newTreeSet();

        if (!EventStructure.isConf(conf))
            throw new RuntimeException("The argument is not a valid configuration.");

        for (final Event e : this.getExtensions(conf)) {
            if(Sets.intersection(e.getDirectConflicts(), conf).isEmpty()) {
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

    /**
     * Get the depth of an event in a given configuration
     *
     * @param conf  the configuration
     * @param event the event
     * @return the depth of the event in the configuration
     */
    public static int getEventDetph(Set<Event> conf, Event event) {
        if (event.getParents().isEmpty())
            return 0;

        int minParentDepth = Integer.MAX_VALUE;
        for (Event parent : event.getParents()) {
            if (conf.contains(parent)) {
                int parentDepth = getEventDetph(conf, parent);
                if (parentDepth < minParentDepth)
                    minParentDepth = parentDepth;
            }
        }
        return minParentDepth + 1;
    }

    /**
     * Create a new event with the given id
     *
     * @param name
     * @return
     */
    public Event newEvent(String name) {
        if (eventSet.containsKey(name)) {
            throw new RuntimeException("Event already in structure.");
        } else {
            Event newEvent = new Event(Integer.parseInt(name));
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

    /**
     * Computes the width of a given configuration (size of its maximum anti-chain) using an exhaustive search approach.
     * It takes a parameter that represents the maximum expected width to bound and speed up the search.
     * It generates all 'maxWidth'-combinations of the events in the configuration and then filters out those that
     * contain any pair of dependent events. The ones remaining are all the maximal anti-chains from which it takes the
     * longest one.
     *
     * @param conf     the configuration
     * @param maxWidth the maximum expected width
     * @return the width of the configuration
     */
    public static Integer getWidth(Set<Event> conf, int maxWidth) {
        int confWidth = Sets.cartesianProduct(Collections.nCopies(maxWidth, conf)).stream().map(set -> ImmutableSet.copyOf(set)).filter(new Predicate<Set<Event>>() {
            public boolean test(Set<Event> eventSet) {
                return Sets.cartesianProduct(eventSet, eventSet).stream().allMatch(new Predicate<List<Event>>() {
                    public boolean test(List<Event> events) {
                        Event a = events.get(0);
                        Event b = events.get(1);
                        return (!a.isDependent(b) && !b.isDependent(a)) || (a.equals(b));
                    }
                });
            }
        }).mapToInt(Set::size).max().orElse(0);

        return confWidth;
    }

    /**
     * Gets a set with the pairs of events that are in direct conflict
     * @return  a set with the pairs of events that are in direct conflict
     */
    public Set<Map.Entry<Event, Event>> getDirectConflicts(final Set<Event> conf, final PrettyPrinter<Map.Entry<Event, Event>> prettyPrinter) {
        return conf.stream().map(
                event -> event.getDirectConflicts().stream().map(
                        eventConflict -> new AbstractMap.SimpleImmutableEntry<Event, Event>(event, eventConflict) {
                            @Override
                            public String toString() {
                                return prettyPrinter.print(this);
                            }

                            @Override
                            public boolean equals(Object o) {
                                if(o instanceof Map.Entry)
                                    return (this.getKey().equals(((Map.Entry<Event,Event>)o).getKey()) && this.getValue().equals(((Map.Entry<Event,Event>)o).getValue())) || (this.getKey().equals(((Map.Entry<Event,Event>)o).getValue()) && this.getValue().equals(((Map.Entry<Event,Event>)o).getKey()));
                                else
                                    return false;
                            }

                            @Override
                            public int hashCode() {
                                return super.hashCode();
                            }
                        })).flatMap(a -> a).collect(Collectors.toSet());
    }

    /**
     * Gets a set with the pairs of events that are immediately dependent
     * @return  a set with the pairs of events that are immediately dependent
     */
    public Set<Map.Entry<Event, Event>> getDirectDeps(final Set<Event> conf, final PrettyPrinter<Map.Entry<Event, Event>> prettyPrinter) {
        return conf.stream().map(
                event -> event.getChilds().stream().filter( child -> conf.contains(child)).map(
                        eventChild -> new AbstractMap.SimpleImmutableEntry<Event, Event>(event, eventChild) {
                            @Override
                            public String toString() {
                                return prettyPrinter.print(this);
                            }
                        })).flatMap(a -> a).collect(Collectors.toCollection(TREE_SET_SUPPLIER));
    }

    @Override
    public String toString() {
        return "< = " + getDirectDeps(this.getEventSet(), BASE_PRINTER) + " # = " + getDirectConflicts(this.getEventSet(), BASE_PRINTER);
    }

    public String toDot(Set<Event> conf) {
        String dotFormat = "strict digraph {\n\tsubgraph deps {\n";
        for(Map.Entry<Event,Event> eventTuple: getDirectDeps(conf, DOT_PRINTER)){
            dotFormat += "\t\t" + eventTuple.toString();
        }
        dotFormat += "\t}\n\tsubgraph conf {\n\t\tedge [dir=none, color=red]\n";
        for(Map.Entry<Event,Event> eventTuple: getDirectConflicts(conf, DOT_PRINTER)){
            dotFormat += "\t\t" + eventTuple.toString();
        }
        return dotFormat + "\t}\n}";
    }

}
