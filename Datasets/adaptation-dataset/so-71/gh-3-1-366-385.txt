/**
 * Copyright 2005-2015 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.impl.repository.mock;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.criteria.AndPredicate;
import org.kuali.rice.core.api.criteria.EqualPredicate;
import org.kuali.rice.core.api.criteria.GreaterThanOrEqualPredicate;
import org.kuali.rice.core.api.criteria.GreaterThanPredicate;
import org.kuali.rice.core.api.criteria.LessThanOrEqualPredicate;
import org.kuali.rice.core.api.criteria.LessThanPredicate;
import org.kuali.rice.core.api.criteria.LikePredicate;
import org.kuali.rice.core.api.criteria.OrPredicate;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

/**
 * A helper class for the Mock implementation to match criteria to values on the
 * object
 *
 * @author nwright
 */
public class CriteriaMatcherInMemory<T> {

    public CriteriaMatcherInMemory() {
        super();
    }
    private QueryByCriteria criteria;

    public QueryByCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(QueryByCriteria criteria) {
        this.criteria = criteria;
    }

    /**
     * finds all of the supplied objects that match the specified criteria
     *
     * @param all
     * @return filtered list
     */
    public Collection<T> findMatching(Collection<T> all) {
        // no criteria means get all
        if (criteria == null) {
            return all;
        }
        int count = -1;
        int startAt = 0;
        if (this.criteria.getStartAtIndex() != null) {
            startAt = this.criteria.getStartAtIndex();
        }
        int maxResults = Integer.MAX_VALUE;
        if (this.criteria.getMaxResults() != null) {
            maxResults = this.criteria.getMaxResults();
        }
        List<T> selected = new ArrayList<T>();
        for (T obj : all) {
            if (matches(obj)) {
                count++;
                if (count < startAt) {
                    continue;
                }
                selected.add(obj);
                if (count > maxResults) {
                    break;
                }
            }
        }
        return selected;
    }

    /**
     * Checks if an object matches the criteria
     *
     * @param infoObject
     * @return
     */
    public boolean matches(T infoObject) {
        return matches(infoObject, this.criteria.getPredicate());
    }

    /**
     * protected for testing
     */
    protected boolean matches(T infoObject, Predicate predicate) {
        // no predicate matches everyting
        if (predicate == null) {
            return true;
        }
        if (predicate instanceof OrPredicate) {
            return matchesOr(infoObject, (OrPredicate) predicate);
        }
        if (predicate instanceof AndPredicate) {
            return matchesAnd(infoObject, (AndPredicate) predicate);
        }
        if (predicate instanceof EqualPredicate) {
            return matchesEqual(infoObject, (EqualPredicate) predicate);
        }
        if (predicate instanceof LessThanPredicate) {
            return matchesLessThan(infoObject, (LessThanPredicate) predicate);
        }
        if (predicate instanceof LessThanOrEqualPredicate) {
            return matchesLessThanOrEqual(infoObject, (LessThanOrEqualPredicate) predicate);
        }
        if (predicate instanceof GreaterThanPredicate) {
            return matchesGreaterThan(infoObject, (GreaterThanPredicate) predicate);
        }
        if (predicate instanceof GreaterThanOrEqualPredicate) {
            return matchesGreaterThanOrEqual(infoObject, (GreaterThanOrEqualPredicate) predicate);
        }
        if (predicate instanceof LikePredicate) {
            return matchesLike(infoObject, (LikePredicate) predicate);
        }
        throw new UnsupportedOperationException("predicate type not supported yet in in-memory mathcer" + predicate.getClass().getName());
    }

    private boolean matchesOr(T infoObject, OrPredicate predicate) {
        for (Predicate subPred : predicate.getPredicates()) {
            if (matches(infoObject, subPred)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesAnd(T infoObject, AndPredicate predicate) {
        for (Predicate subPred : predicate.getPredicates()) {
            if (!matches(infoObject, subPred)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesEqual(T infoObject, EqualPredicate predicate) {
        Object dataValue = extractValue(predicate.getPropertyPath(), infoObject);
        return matchesEqual(dataValue, predicate.getValue().getValue());
    }

    private boolean matchesLessThan(T infoObject, LessThanPredicate predicate) {
        Object dataValue = extractValue(predicate.getPropertyPath(), infoObject);
        return matchesLessThan(dataValue, predicate.getValue().getValue());
    }

    private boolean matchesLessThanOrEqual(T infoObject, LessThanOrEqualPredicate predicate) {
        Object dataValue = extractValue(predicate.getPropertyPath(), infoObject);
        if (matchesLessThan(dataValue, predicate.getValue().getValue())) {
            return true;
        }
        return matchesEqual(dataValue, predicate.getValue().getValue());
    }

    private boolean matchesGreaterThan(T infoObject, GreaterThanPredicate predicate) {
        Object dataValue = extractValue(predicate.getPropertyPath(), infoObject);
        return matchesGreaterThan(dataValue, predicate.getValue().getValue());
    }

    private boolean matchesGreaterThanOrEqual(T infoObject, GreaterThanOrEqualPredicate predicate) {
        Object dataValue = extractValue(predicate.getPropertyPath(), infoObject);
        if (matchesGreaterThan(dataValue, predicate.getValue().getValue())) {
            return true;
        }
        return matchesEqual(dataValue, predicate.getValue().getValue());
    }

    private boolean matchesLike(T infoObject, LikePredicate predicate) {
        Object dataValue = extractValue(predicate.getPropertyPath(), infoObject);
        return matchesLike(dataValue, predicate.getValue().getValue());
    }

    protected static Object extractValue(String fieldPath, Object infoObject) {

        try {
            if (infoObject == null) {
                return null;
            }
            Object value = PropertyUtils.getNestedProperty(infoObject, fieldPath);
            // translate boolean to string so we can compare
            // Have to do this because RICE's predicate does not support boolean 
            // because it is database oriented and most DB do not support booleans natively.
            if (value instanceof Boolean) {
                return value.toString();
            }
            // See Rice's CriteriaSupportUtils.determineCriteriaValue where data normalized 
            // translate date to joda DateTime because that is what RICE PredicateFactory does 
            // similar to rest of the types 
            if (value instanceof Date) {
                return new DateTime ((Date) value);
            }
            if (value instanceof Calendar) {
                return new DateTime ((Calendar) value);
            }
            if (value instanceof Short) {
                return BigInteger.valueOf(((Short) value).longValue());
            }
            if (value instanceof AtomicLong) {
                return BigInteger.valueOf(((AtomicLong) value).longValue());
            }
            if (value instanceof AtomicInteger) {
                return BigInteger.valueOf(((AtomicInteger) value).longValue());
            }
            if (value instanceof Integer) {
                return BigInteger.valueOf(((Integer)value).longValue());
            }
            if (value instanceof Long) {
                return BigInteger.valueOf(((Long)value).longValue());
            }
            if (value instanceof Float) {
                return BigDecimal.valueOf(((Float)value).doubleValue());
            }
            if (value instanceof Double) {
                return BigDecimal.valueOf(((Double)value).doubleValue());
            }
            return value;
        } catch (NestedNullException ex) {
            return null;
        }  catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(fieldPath, ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalArgumentException(fieldPath, ex);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(fieldPath, ex);
        }
//        }
//        return value;
    }

    public static boolean matchesEqual(Object dataValue, Object criteriaValue) {
        if (dataValue == criteriaValue) {
            return true;
        }
        if (dataValue == null && criteriaValue == null) {
            return true;
        }
        if (dataValue == null) {
            return false;
        }
        return dataValue.equals(criteriaValue);
    }

    public static boolean matchesLessThan(Object dataValue, Object criteriaValue) {
        if (dataValue == criteriaValue) {
            return false;
        }
        if (dataValue == null && criteriaValue == null) {
            return false;
        }
        if (dataValue == null) {
            return false;
        }
        if (criteriaValue instanceof Comparable) {
            Comparable comp1 = (Comparable) dataValue;
            Comparable comp2 = (Comparable) criteriaValue;
            if (comp1.compareTo(comp2) < 0) {
                return true;
            }
            return false;
        }
        throw new IllegalArgumentException("The values are not comparable " + criteriaValue);
    }

    public static boolean matchesGreaterThan(Object dataValue, Object criteriaValue) {
        if (dataValue == criteriaValue) {
            return false;
        }
        if (dataValue == null && criteriaValue == null) {
            return false;
        }
        if (dataValue == null) {
            return false;
        }
        if (criteriaValue instanceof Comparable) {
            Comparable comp1 = (Comparable) dataValue;
            Comparable comp2 = (Comparable) criteriaValue;
            if (comp1.compareTo(comp2) > 0) {
                return true;
            }
            return false;
        }
        throw new IllegalArgumentException("The values are not comparable " + criteriaValue);
    }
    // cache
    private transient Map<String, Pattern> patternCache = new HashMap<String, Pattern>();

    private Pattern getPattern(String expr) {
        Pattern p = patternCache.get(expr);
        if (p == null) {
            p = compilePattern(expr);
            patternCache.put(expr, p);
        }
        return p;
    }

    public boolean matchesLike(Object dataValue, Object criteriaValue) {
        if (dataValue == criteriaValue) {
            return false;
        }
        if (dataValue == null && criteriaValue == null) {
            return false;
        }
        if (dataValue == null) {
            return false;
        }
        return matchesLikeCachingPattern(dataValue.toString(), criteriaValue.toString());
    }

    /**
     * this was taken from
     * http://stackoverflow.com/questions/898405/how-to-implement-a-sql-like-like-operator-in-java
     */
    public boolean matchesLikeCachingPattern(final String str, final String expr) {
        return matchesLike(str, getPattern(expr));
    }

    private static Pattern compilePattern(final String expr) {
        String regex = quotemeta(expr);
        regex = regex.replace("_", ".").replace("%", ".*?");
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        return p;
    }

    /**
     * This was taken from
     *
     * http://stackoverflow.com/questions/898405/how-to-implement-a-sql-like-like-operator-in-java
     */
    public static boolean matchesLike(final String str, final String expr) {
        Pattern p = compilePattern(expr);
        return matchesLike(str, p);
    }

    private static boolean matchesLike(final String str, final Pattern p) {
        return p.matcher(str).matches();
    }

    private static String quotemeta(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String cannot be null");
        }

        int len = s.length();
        if (len == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if ("[](){}.*+?$^|#\\".indexOf(c) != -1) {
                sb.append("\\");
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
