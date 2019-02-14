public class foo{
      private void processClick(MouseEvent event)
      {
          jTextComponent = (JTextComponent) event.getSource();

          boolean enableUndo = undoManager.canUndo();
          boolean enableRedo = undoManager.canRedo();
          boolean enableCut = false;
          boolean enableCopy = false;
          boolean enablePaste = false;
          boolean enableDelete = false;
          boolean enableSelectAll = false;

          String selectedText = jTextComponent.getSelectedText();
          String text = jTextComponent.getText();

          if (text != null)
          {
              if (text.length() > 0)
              {
                  enableSelectAll = true;
              }
          }

          if (selectedText != null)
          {
              if (selectedText.length() > 0)
              {
                  enableCut = true;
                  enableCopy = true;
                  enableDelete = true;
              }
          }

          try
          {
              if (clipboard.getData(DataFlavor.stringFlavor) != null)
              {
                  enablePaste = true;
              }
          } catch (Exception exception)
          {
              exception.printStackTrace();
          }

          undo.setEnabled(enableUndo);
          redo.setEnabled(enableRedo);
          cut.setEnabled(enableCut);
          copy.setEnabled(enableCopy);
          paste.setEnabled(enablePaste);
          delete.setEnabled(enableDelete);
          selectAll.setEnabled(enableSelectAll);

          show(jTextComponent, event.getX(), event.getY());
      }
}