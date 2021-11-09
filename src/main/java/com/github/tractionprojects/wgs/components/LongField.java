package com.github.tractionprojects.wgs.components;

import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.function.SerializableFunction;

public class LongField extends AbstractNumberField<LongField, Long>
{
    private static final SerializableFunction<String, Long> PARSER = (valueFormClient) ->
    {
        if (valueFormClient != null && !valueFormClient.isEmpty())
        {
            try
            {
                return Long.parseLong(valueFormClient);
            } catch (NumberFormatException var2)
            {
                return null;
            }
        } else
        {
            return null;
        }
    };
    private static final SerializableFunction<Long, String> FORMATTER = (valueFromModel) ->
            valueFromModel == null ? "" : valueFromModel.toString();

    public LongField()
    {
        super(PARSER, FORMATTER, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public LongField(SerializableFunction<String, Long> parser, SerializableFunction<Long, String> formatter, double absoluteMin, double absoluteMax)
    {
        super(parser, formatter, absoluteMin, absoluteMax);
    }

    public LongField(String label)
    {
        this();
        this.setLabel(label);
    }
}
