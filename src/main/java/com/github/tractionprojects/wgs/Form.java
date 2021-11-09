package com.github.tractionprojects.wgs;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.Converter;

public abstract class Form<T>
{
    private final FormLayout layout = new FormLayout();
    private final Binder<T> binder;

    public Form(Class<T> type)
    {
        binder = new Binder<>(type);
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
    }

    protected <F extends AbstractField<?, S>, S> F addField(String label, F component, String field)
    {
        return addField(label, component, field, false, null);
    }

    protected <F extends AbstractField<?, S>, S> F addField(String label, F component, String field, boolean required)
    {
        return addField(label, component, field, required, null);
    }

    protected <F extends AbstractField<?, S>, S> F addField(String label, F component, String field, Converter<S, ?> converter)
    {
        return addField(label, component, field, false, converter);
    }

    protected <F extends AbstractField<?, S>, S> F addField(String label, F component, String field, boolean required, Converter<S, ?> converter)
    {
        layout.addFormItem(component, label);
        Binder.BindingBuilder<T, S> b = binder.forField(component);
        if (converter != null)
            b.withConverter(converter);
        if (required)
            b = b.asRequired(label + " is required.");
        b.bind(field);
        return component;
    }

    public void open(T data)
    {
        binder.setBean(data);
        Dialog dialog = new Dialog();
        dialog.setWidth("75%");
        dialog.setCloseOnOutsideClick(false);
        dialog.add(layout, createButtons(dialog));
        dialog.open();
    }

    private HorizontalLayout createButtons(Dialog dialog)
    {
        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e -> save(dialog));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(save, cancel);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        return buttons;
    }

    private void save(Dialog dialog)
    {
        if (saveData(binder.getBean()))
            dialog.close();
    }

    abstract protected boolean saveData(T data);
}
