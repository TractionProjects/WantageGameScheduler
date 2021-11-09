package com.github.tractionprojects.wgs.components;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.Set;
import java.util.function.Function;

public class SetConverter<T> implements Converter<String, Set<T>>
{
    private final Function<T, String> objectToString;

    public SetConverter(Function<T, String> objectToString)
    {
        this.objectToString = objectToString;
    }

    @Override
    public Result<Set<T>> convertToModel(String value, ValueContext context)
    {
        return null;
    }

    @Override
    public String convertToPresentation(Set<T> set, ValueContext context)
    {
        if (set.size() == 0)
            return "";
        StringBuilder retVal = new StringBuilder();
        for (T object : set)
            retVal.append(objectToString.apply(object)).append("\n");
        return retVal.substring(0, retVal.length() - 1);
    }
}
