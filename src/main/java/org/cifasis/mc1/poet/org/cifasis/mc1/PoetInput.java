package org.cifasis.mc1.poet.org.cifasis.mc1;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.cifasis.mc1.EventStructure;
import org.cifasis.mc1.PoetBaseVisitor;
import org.cifasis.mc1.PoetParser;

/**
 * Created by Cristian on 08/09/15.
 */
public class PoetInput extends PoetBaseVisitor<EventStructure> {

    private final EventStructure eventStructure;

    public PoetInput(EventStructure eventStructure) {
        this.eventStructure = eventStructure;
    }

    @Override
    public EventStructure visitEvents(PoetParser.EventsContext ctx) {

        for(PoetParser.EventContext eventContext: ctx.event()) {
            visitEvent(eventContext);
        }
        return eventStructure;
    }

    @Override
    public EventStructure visitEvent(PoetParser.EventContext ctx) {
        EventStructure.Event newEvent = eventStructure.newEvent(ctx.INT().getText());

        // Add dependences
        for(TerminalNode predEventName : ctx.pred().eventList().INT()) {
            EventStructure.Event predEvent = null;
            if(predEventName.getText().equals("0"))
                predEvent = eventStructure.getRoot();
            else
                predEvent = eventStructure.getEventByName(predEventName.getText());

            newEvent.dependsOn(predEvent);
        }

        // Add conflictsWith
        for(TerminalNode conflictEventName : ctx.icnf().eventList().INT()) {
            if (Integer.valueOf(conflictEventName.getText()) < newEvent.getId()) {
                EventStructure.Event conflictEvent = null;
                if (conflictEventName.getText().equals("0"))
                    conflictEvent = eventStructure.getRoot();
                else
                    conflictEvent = eventStructure.getEventByName(conflictEventName.getText());

                newEvent.conflictsWith(conflictEvent);
            }
        }

        return eventStructure;
    }
}
