public class foo {
    public TypedComboBox(Composite parent) {
        this.viewer = new ComboViewer(parent, SWT.READ_ONLY);
        this.viewer.setContentProvider(ArrayContentProvider.getInstance());

        viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                T typedElement = getTypedObject(element);
                if (labelProvider != null && typedElement != null) {
                    if (typedElement == currentSelection) {
                        return labelProvider.getSelectedLabel(typedElement);
                    } else {
                        return labelProvider.getListLabel(typedElement);
                    }

                } else {
                    return element.toString();
                }
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event
                        .getSelection();
                T typedSelection = getTypedObject(selection.getFirstElement());
                if (typedSelection != null) {
                    currentSelection = typedSelection;
                    viewer.refresh();
                    notifySelectionListeners(typedSelection);
                }

            }
        });

        this.content = new ArrayList<T>();
        this.selectionListeners = new ArrayList<TypedComboBoxSelectionListener<T>>();
    }
}