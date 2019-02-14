public class foo{
                @Override
                public void onChildViewAdded(View parent, View child) { // apply text size every time when a new child view is added
                    try {
                        Object object = child;
                        Field[] fields = object.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            if (field.getName().equals("mMonthNumDrawPaint")) { // the paint is stored inside the view
                                field.setAccessible(true);
                                object = field.get(object);
                                Method method = object.getClass().
                                        getDeclaredMethod("setTextSize", float.class); // finally set text size
                                method.setAccessible(true);
                                method.invoke(object, (Object) mDateTextSize);

                                break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("DateTimeWidget", e.getMessage(), e);
                    }
                }
}