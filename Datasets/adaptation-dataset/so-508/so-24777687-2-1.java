public class foo {
    public Component getListCellRendererComponent(
        JList<? extends JCheckBox> list, JCheckBox value, int index,
        boolean isSelected, boolean cellHasFocus) {
      JCheckBox checkbox = value;

      //Drawing checkbox, change the appearance here
      checkbox.setBackground(isSelected ? getSelectionBackground()
          : getBackground());
      checkbox.setForeground(isSelected ? getSelectionForeground()
          : getForeground());
      checkbox.setEnabled(isEnabled());
      checkbox.setFont(getFont());
      checkbox.setFocusPainted(false);
      checkbox.setBorderPainted(true);
      checkbox.setBorder(isSelected ? UIManager
          .getBorder("List.focusCellHighlightBorder") : noFocusBorder);
      return checkbox;
    }
}