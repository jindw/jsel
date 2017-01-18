package org.xidea.el.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ReflectUtil {
	private static final Log log = LogFactory.getLog(ReflectUtil.class);
	private static final String LENGTH = "length";
	private static final Map<Class<?>, Map<String, Method>> readerMap = newMap();
	private static final Map<Class<?>, Map<String, Method>> writerMap = newMap();
	/* 属性类型， 属性申明类的Class*/
	private static final Map<Class<?>, Map<String, Type[]>> typeMap = newMap();
	private static final Map<Class<?>, Map<String, Field>> fieldMap = newMap();
	private static final Map<Class<?>, Constructor<?>> constructorMap = newMap();
	private static final Map<Class<? extends Enum<?>>, Enum<?>[]> enumMap = newMap();
	private static Object initLock = new Object();
//	private static ClassLoader defaultLoader = ReflectUtil.class
//			.getClassLoader();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> T newMap() {
		return (T) new HashMap();// WeakHashMap();
	}

	public static Map<String, Method> getGetterMap(final Class<?> clazz) {
		Map<String, Method> propertyMap = readerMap.get(clazz);
		if (propertyMap == null) {
			initProperties(clazz);
			propertyMap = readerMap.get(clazz);
		}
		return propertyMap;
	}

	public static Map<String, Method> getSetterMap(final Class<?> clazz) {
		Map<String, Method> propertyMap = writerMap.get(clazz);
		if (propertyMap == null) {
			initProperties(clazz);
			propertyMap = writerMap.get(clazz);
		}
		return propertyMap;
	}

	private static Map<String, Type[]> getTypeMap(final Class<?> clazz) {
		Map<String, Type[]> tMap = typeMap.get(clazz);
		if (tMap == null) {
			initProperties(clazz);
			tMap = typeMap.get(clazz);
		}
		return tMap;
	}

	public static Map<String, Field> getFieldMap(final Class<?> clazz) {
		Map<String, Field> fMap = fieldMap.get(clazz);
		if (fMap == null) {
			initProperties(clazz);
			fMap = fieldMap.get(clazz);
		}
		return fMap;
	}

	public static Set<String> getPropertySet(final Class<?> clazz) {
		return getTypeMap(clazz).keySet();
	}

	private static void initProperties(final Class<?> clazz) {
		synchronized (initLock) {
			try {
				Constructor<?> cons = clazz.getDeclaredConstructor();
				if (!cons.isAccessible()) {
					cons.setAccessible(true);
				}
				constructorMap.put(clazz, cons);
			} catch (Exception e) {
			}
			HashMap<String, Method> getterMap = new HashMap<String, Method>();
			HashMap<String, Method> setterMap = new HashMap<String, Method>();
			HashMap<String, Type[]> propertyMap = new HashMap<String, Type[]>();
			HashMap<String, Field> fieldMap_ = new HashMap<String, Field>();
			try {
				if (!clazz.equals(Object.class)) {
					getterMap.putAll(getGetterMap(clazz.getSuperclass()));
					setterMap.putAll(getSetterMap(clazz.getSuperclass()));
					propertyMap.putAll(getTypeMap(clazz.getSuperclass()));
					fieldMap_.putAll(getFieldMap(clazz.getSuperclass()));
				}
				Method[] methods = clazz.getDeclaredMethods();

				for (Method m : methods) {
					if ((m.getModifiers() & Modifier.PUBLIC) > 0) {
						Class<? extends Object> type = m.getReturnType();
						Class<? extends Object>[] params = m
								.getParameterTypes();
						String name = m.getName();
						if (type == Void.TYPE) {
							if (params.length == 1 && name.startsWith("set")) {
								type = params[0];
								initMethod(setterMap, propertyMap, m,
										name.substring(3));
							}
						} else {
							if (params.length == 0) {
								if (name.startsWith("get")
										&& !name.equals("getClass")) {
									initMethod(getterMap, propertyMap, m,
											name.substring(3));
								} else if (type == Boolean.TYPE
										&& name.startsWith("is")) {
									initMethod(getterMap, propertyMap, m,
											name.substring(2));
								}
							}
						}
					}
				}
				// boolean nogs = propertyMap.isEmpty();
				boolean isMember = clazz.isMemberClass();

				Field[] fields = clazz.getDeclaredFields();
				for (Field f : fields) {
					f.setAccessible(true);
					String name = f.getName();
					if (// nogs&&
					(isMember || 0 < (f.getModifiers() & Modifier.PUBLIC))) {
						propertyMap.put(name, new Type[] { GenericFinder.Default.getGenericType(f),
								clazz });
					}
					fieldMap_.put(name, f);
				}
			} catch (Exception e) {
				log.warn("初始化属性集合异常", e);
			} finally {
				readerMap.put(clazz, Collections.unmodifiableMap(getterMap));
				writerMap.put(clazz, Collections.unmodifiableMap(setterMap));
				typeMap.put(clazz, Collections.unmodifiableMap(propertyMap));
				fieldMap.put(clazz, Collections.unmodifiableMap(fieldMap_));
			}
		}
	}


	private static void initMethod(Map<String, Method> propertyMap,
			Map<String, Type[]> typeMap, Method m, String name) {
		if (name.length() > 0) {
			char c = name.charAt(0);
			if (Character.isUpperCase(c)) {
				name = Character.toLowerCase(c) + name.substring(1);
				m.setAccessible(true);
				propertyMap.put(name, m);
				Type type = GenericFinder.Default.getGenericReturnType(m);
				if (type == Void.TYPE) {
					type = m.getParameterTypes()[0];
				}
				Type[] ot = typeMap.get(name);
				if (ot != null) {
					if (ot[0] != type) {
						log.warn("属性类型冲突：" + ot + "!=" + type);
					}
				}
				typeMap.put(name, new Type[] { type, m.getDeclaringClass() });
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static Map<String, ? extends Object> map(final Object context) {
		if (context == null) {
			return Collections.EMPTY_MAP;
		} else if (context instanceof Map) {
			return (Map<String, ? extends Object>) context;
		}
		return new ProxyMap(context, getTypeMap(context.getClass()).keySet());
	}

	private static int toIndex(Object key) {
		return key instanceof Number ? ((Number) key).intValue() : Integer
				.parseInt(String.valueOf(key));
	}

	public static Class<? extends Object> getValueType(Type type) {
		Type result = null;
		Class<? extends Object> clazz = null;
		if (type instanceof ParameterizedType) {
			clazz = (Class<?>) ((ParameterizedType) type).getRawType();
		} else {
			clazz = (Class<?>) type;
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			result = getParameterizedType(type, Collection.class, 0);
		} else if (Map.class.isAssignableFrom(clazz)) {
			result = getParameterizedType(type, Map.class, 1);
		}
		if (result != null) {
			return baseClass(result);
		}
		return Object.class;
	}

	public static Class<?> getKeyType(Type type) {
		Class<?> clazz = null;
		if (type instanceof ParameterizedType) {
			clazz = (Class<?>) ((ParameterizedType) type).getRawType();
		} else {
			clazz = (Class<?>) type;
		}
		if (Map.class.isAssignableFrom(clazz)) {
			Type result = getParameterizedType(type, Map.class, 0);
			if (result != null) {
				return baseClass(result);
			}
		}
		return Integer.TYPE;
	}

	public static Type getParameterizedType(final Type ownerType,
			final Class<?> declaredClass, int paramIndex) {
		return GenericFinder.Default.getParameterizedType(ownerType, declaredClass, paramIndex);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T newInstance(Class<T> clazz) {
		try {
			Constructor cons = constructorMap.get(clazz);
			if (cons != null) {
				initProperties(clazz);
				cons = constructorMap.get(clazz);
				if (cons != null) {
					return (T) cons.newInstance();
				}
			}
			try {
				return clazz.newInstance();
			} catch (java.lang.Exception e) {
				try {
					Constructor cuns = clazz.getDeclaredConstructor();
					if (!cuns.isAccessible()) {
						cuns.setAccessible(true);
						constructorMap.put(clazz, cuns);
						return (T) cuns.newInstance();
					}
				} catch (NoSuchMethodException ne) {
					if (clazz.isMemberClass()) {
						log.error("内部内 class " + clazz
								+ " 无默认构造函数，无法创建（非static的内部内，不可能拥有默认构造函数）。");
					}
				}
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Class<? extends Object> baseClass(Type result) {
		if (result instanceof Class<?>) {
			return (Class<?>) result;
		} else if (result instanceof ParameterizedType) {
			return baseClass(((ParameterizedType) result).getRawType());
		} else if (result instanceof WildcardType) {
			return baseClass(((WildcardType) result).getUpperBounds()[0]);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Enum<?>> T getEnum(Object value, Class<T> clazz) {
		if (value instanceof String) {
			Class clazz2 = clazz;
			return (T) Enum.valueOf(clazz2, (String) value);
		} else if (value instanceof Number) {
			Enum[] es = enumMap.get(clazz);
			try {
				if (es == null) {
					Method method = clazz.getMethod("values");
					enumMap.put(clazz, es = (Enum[]) method.invoke(null));
				}
				int i = ((Number) value).intValue();
				return (T) es[i];
			} catch (Exception e1) {
			}
		}
		return null;
	}

	public static Class<?> getPropertyClass(Type type, Object key) {
		return baseClass(getPropertyType(type, key));
	}

	public static Type getPropertyType(Type ownerType, Object key) {
		Class<?> clazz = baseClass(ownerType);
		if (clazz != null) {
			if (Collection.class.isAssignableFrom(clazz)) {
				return getValueType(ownerType);
			} else if (Map.class.isAssignableFrom(clazz)) {
				return getValueType(ownerType);
			} else if (clazz.isArray()) {
				if (LENGTH.equals(key)) {
					return Integer.TYPE;
				} else if (Number.class.isInstance(key)) {
					return clazz.getComponentType();
				}
			} else {
				Type typeAndClass[] = getTypeMap(clazz).get(String.valueOf(key));
				if (typeAndClass != null) {
					return GenericFinder.Default.getParameterizedType(ownerType, (Class<?>) typeAndClass[1], typeAndClass[0]);
				}
			}
		}
		return null;
	}

	public static Object getValue(Object context, Object key) {
		Class<? extends Object> clazz = context.getClass();
		if (context != null) {
			try {
				if (context instanceof Map<?, ?>) {
					return ((Map<?, ?>) context).get(key);
				}
				if (key instanceof String) {
					Method method = getGetterMap(clazz).get((String) key);
					if (method == null) {
						Field field;
						if (context instanceof Class<?>) {
							field = getFieldMap((Class<?>) context).get(key);
						} else {
							field = getFieldMap(clazz).get(key);

						}
						if (field != null) {
							return field.get(context);
						}
					} else {
						return method.invoke(context);
					}
					if (LENGTH.equals(key)) {
						if (clazz.isArray()) {
							return Array.getLength(context);
						} else if (context instanceof Collection<?>) {
							return ((Collection<?>) context).size();
						} else if (context instanceof String) {
							return ((String) context).length();
						}
					}
					if (context instanceof Map<?, ?>) {
						return ((Map<?, ?>) context).get(key);
					} else if (clazz.isArray()) {
						return Array.getLength(context);
					} else {
						return Array.get(context, toIndex(key));
					}
				} else if (context instanceof List<?>) {
					return ((List<?>) context).get(toIndex(key));
				}

			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(e);
				}
			}
		}
		return null;
	}

	public static void setValues(Object base, Map<String, Object> attributeMap) {
		for (String key : attributeMap.keySet()) {
			ReflectUtil.setValue(base, key, attributeMap.get(key));
		}
	}

	@SuppressWarnings("unchecked")
	public static void setValue(Object base, Object key, Object value) {
		if (base != null) {
			try {
				Class<? extends Object> clazz = base.getClass();
				if (clazz.isArray()) {
					Array.set(base, toIndex(key), value);
				} else if (base instanceof List<?>) {
					((List<Object>) base).set(toIndex(key), value);
				}
				if (base instanceof Map) {
					((Map<Object, Object>) base).put(key, value);
				}
				String name = String.valueOf(key);
				Method method = getSetterMap(clazz).get(name);
				if (method != null) {
					if (value != null) {
						Class<?> type = method.getParameterTypes()[0];
						value = toWrapper(value, type);
					}
					method.invoke(base, value);
				} else {
					Field field = fieldMap.get(clazz).get(name);
					if (value != null) {
						Class<?> type = field.getType();
						value = toWrapper(value, type);
					}
					field.set(base, value);
				}
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(e);
				}
			}
		}

	}

	private static Object toWrapper(Object value, Class<?> type) {
		if (!type.isInstance(value)) {
			type = toWrapper(type);
			if (Number.class.isAssignableFrom(type)) {
				value = toValue((Number) value, type);
			}
		}
		return value;
	}

	public static Number toValue(Number value, Class<? extends Object> type) {
		if (type == Long.class) {
			return value.longValue();
		} else if (type == Integer.class) {
			return value.intValue();
		} else if (type == Short.class) {
			return value.shortValue();
		} else if (type == Byte.class) {
			return value.byteValue();
		} else if (type == Double.class) {
			return value.doubleValue();
		} else if (type == Float.class) {
			return value.floatValue();
		} else {
			Class<? extends Object> clazz = ReflectUtil.toWrapper(type);
			if (clazz == type) {
				return null;
			} else {
				return toValue(value, clazz);
			}
		}
	}

	public final static Class<? extends Object> toWrapper(
			Class<? extends Object> type) {
		if (type.isPrimitive()) {
			if (Byte.TYPE == type) {
				return Byte.class;
			} else if (Short.TYPE == type) {
				return Short.class;
			} else if (Integer.TYPE == type) {
				return Integer.class;
			} else if (Long.TYPE == type) {
				return Long.class;
			} else if (Float.TYPE == type) {
				return Float.class;
			} else if (Double.TYPE == type) {
				return Double.class;
			} else if (Character.TYPE == type) {
				return Character.class;
			} else if (Boolean.TYPE == type) {
				return Boolean.class;
			}
		}
		return type;
	}

	public static void setGenericFinder(GenericFinder finder) {
		GenericFinder.Default.finder = finder;
	}
}
