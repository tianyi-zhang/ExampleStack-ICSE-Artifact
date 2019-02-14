public class foo {
public void foo(){
Notification notification = (Notification) event.getParcelableData();
    RemoteViews views = notification.contentView;
    Class secretClass = views.getClass();

    try {
        Map<Integer, String> text = new HashMap<Integer, String>();

        Field outerFields[] = secretClass.getDeclaredFields();
        for (int i = 0; i < outerFields.length; i++) {
            if (!outerFields[i].getName().equals("mActions")) continue;

            outerFields[i].setAccessible(true);

            ArrayList<Object> actions = (ArrayList<Object>) outerFields[i]
                    .get(views);
            for (Object action : actions) {
                Field innerFields[] = action.getClass().getDeclaredFields();

                Object value = null;
                Integer type = null;
                Integer viewId = null;
                for (Field field : innerFields) {
                    field.setAccessible(true);
                    if (field.getName().equals("value")) {
                        value = field.get(action);
                    } else if (field.getName().equals("type")) {
                        type = field.getInt(action);
                    } else if (field.getName().equals("viewId")) {
                        viewId = field.getInt(action);
                    }
                }

                if (type == 9 || type == 10) {
                    text.put(viewId, value.toString());
                }
            }

            System.out.println("title is: " + text.get(16908310));
            System.out.println("info is: " + text.get(16909082));
            System.out.println("text is: " + text.get(16908358));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}