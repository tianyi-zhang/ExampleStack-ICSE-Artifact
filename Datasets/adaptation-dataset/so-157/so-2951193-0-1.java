public class foo {
public static int getUnixPID(Process process) throws Exception
{
    System.out.println(process.getClass().getName());
    if (process.getClass().getName().equals("java.lang.UNIXProcess"))
    {
        Class cl = process.getClass();
        Field field = cl.getDeclaredField("pid");
        field.setAccessible(true);
        Object pidObject = field.get(process);
        return (Integer) pidObject;
    } else
    {
        throw new IllegalArgumentException("Needs to be a UNIXProcess");
    }
}
}