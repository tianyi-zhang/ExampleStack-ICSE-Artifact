package com.szagurskii.rxclipboard;

import com.szagurskii.rxclipboard.internal.Preconditions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Savelii Zagurskii
 */
public class PreconditionsTests {
  @Before public void setup() {
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowIfNull() {
    Preconditions.checkNotNull(null, "Method should throw NullPointer.");
  }

  @Test public void shouldReturnTheSame() {
    String what = Preconditions.checkNotNull("Hey", "Not-null");
    assertThat(what).isEqualTo("Hey");
  }

  @Test public void constructorShouldBePrivate()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    Constructor<Preconditions> constructor = Preconditions.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test public void checkUtilityClass()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    assertUtilityClassWellDefined(Preconditions.class);
  }

  /**
   * Verifies that a utility class is well defined.
   * <br>
   * Author: <a href="https://stackoverflow.com/users/242042/archimedes-trajano">Archimedes Trajano</a>
   *
   * @param clazz utility class to verify.
   * @see <a href="http://stackoverflow.com/a/10872497/4271064">http://stackoverflow.com/a/10872497/4271064</a>
   */
  public static void assertUtilityClassWellDefined(Class<?> clazz)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    Assert.assertTrue("Class must be final.", Modifier.isFinal(clazz.getModifiers()));
    Assert.assertEquals("There must be only one constructor.", 1, clazz.getDeclaredConstructors().length);

    Constructor<?> constructor = clazz.getDeclaredConstructor();
    if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
      Assert.fail("Constructor is not private.");
    }
    constructor.setAccessible(true);
    constructor.newInstance();
    constructor.setAccessible(false);

    for (Method method : clazz.getMethods()) {
      if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)) {
        Assert.fail("There exists a non-static method:" + method);
      }
    }
  }
}
