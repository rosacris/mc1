package org.cifasis.mc1;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by cristian on 21/10/15.
 */
public class Event implements Comparable<Event> {
    private final int id;
    private final List<Event> parents;
    private final List<Event> childs;
    private final Set<Event> conflicts;

    public Event(int id) {
        this.id = id;
        this.parents = Lists.newArrayList();
        this.childs = Lists.newArrayList();
        this.conflicts = Sets.newHashSet();
    }

    public int getId() {
        return id;
    }

    public List<Event> getParents() {
        return parents;
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

    public Event conflictsWith(Event that) {
        this.conflicts.add(that);
        that.conflicts.add(this);
        return this;
    }

    public Set<Event> getDirectConflicts() {
        return conflicts;
    }

    public Set<Event> getCone() {
        Set<Event> cone = Sets.newHashSet(this);
        for (Event e : parents) {
            cone.add(e);
            cone.addAll(e.getCone());
        }
        return cone;
    }

    public Set<Event> getPredecessors(){
        Set<Event> pred = this.getCone();
        pred.remove(this);
        return pred;
    }

    public List<Event> getChilds() {
        return childs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equal(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(Event o) {
        return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
