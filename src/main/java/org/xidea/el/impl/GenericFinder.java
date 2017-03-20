package org.xidea.el.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public interface GenericFinder {
	Type getGenericSuperclass(Class<?> clazz) ;
	Type[] getGenericInterfaces(Class<?> clazz) ;
	Type getGenericType(Field f) ;
	Type getGenericReturnType(Method m) ;
	class Default {
		static GenericFinder finder ;
		static Type getGenericSuperclass(Class<?> clazz) {
			return finder == null?clazz.getGenericSuperclass():finder.getGenericSuperclass(clazz);
		}

		static Type[] getGenericInterfaces(Class<?> clazz) {

			return finder == null?clazz.getGenericInterfaces():finder.getGenericInterfaces(clazz);
		}

		static Type getGenericType(Field f) {
			return finder == null?f.getGenericType():finder.getGenericType(f);
		}

		static Type getGenericReturnType(Method m) {
			return finder == null?m.getGenericReturnType():finder.getGenericReturnType(m);
		}

		static Type getParameterizedType(final Type ownerType,
				final Class<?> declaredClass, int paramIndex) {
			Class<?> clazz = null;
			ParameterizedType pt = null;
			Type[] ats = null;
			TypeVariable<?>[] tps = null;
			if (ownerType instanceof ParameterizedType) {
				pt = (ParameterizedType) ownerType;
				clazz = (Class<?>) pt.getRawType();
				ats = pt.getActualTypeArguments();
				tps = clazz.getTypeParameters();
			} else {
				clazz = (Class<?>) ownerType;
			}
			if (declaredClass == clazz) {
				if (ats != null) {
					return ats[paramIndex];
				}
				return Object.class;
			}
			Class<?>[] ifs = clazz.getInterfaces();
			for (int i = 0; i < ifs.length; i++) {
				Class<?> ifc = ifs[i];
				if (declaredClass.isAssignableFrom(ifc)) {
					return getTureType(
							getParameterizedType(
									getGenericInterfaces(clazz)[i],
									declaredClass, paramIndex), tps, ats);
				}
			}
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null) {
				if (declaredClass.isAssignableFrom(superClass)) {
					return getTureType(
							getParameterizedType(getGenericSuperclass(clazz),
									declaredClass, paramIndex), tps, ats);
				}
			}
			throw new IllegalArgumentException("查找真实类型失败:" + ownerType);
		}

		private static Type getTureType(Type type,
				TypeVariable<?>[] typeVariables, Type[] actualTypes) {

			if (type instanceof TypeVariable<?>) {
				TypeVariable<?> tv = (TypeVariable<?>) type;
				String name = tv.getName();
				if (actualTypes != null) {
					for (int i = 0; i < typeVariables.length; i++) {
						if (name.equals(typeVariables[i].getName())) {
							return actualTypes[i];
						}
					}
				}
				return tv;
				// }else if (type instanceof Class<?>) {
				// return type;
			} else if (type instanceof GenericArrayType) {
				Type ct = ((GenericArrayType) type).getGenericComponentType();
				if (ct instanceof Class<?>) {
					return Array.newInstance((Class<?>) ct, 0).getClass();
				}
			}
			return type;
		}

		/**
		 * @param ownerType 类型
		 * @param declaredClass 申明所在类
		 * @param declaredType 申明所在类的申明属性Type
		 * @return
		 */
		static Type getParameterizedType(final Type ownerType,
				final Class<?> declaredClass, final Type declaredType) {
			if (declaredType instanceof TypeVariable) {
				String name = ((TypeVariable<?>) declaredType).getName();
				TypeVariable<?>[] typeVariables = declaredClass.getTypeParameters();
				if (typeVariables != null) {
					for (int i = 0; i < typeVariables.length; i++) {
						if (name.equals(typeVariables[i].getName())) {
							return getParameterizedType(ownerType, declaredClass, i);
						}
					}
				}
				return declaredType;
			} else if (declaredType instanceof ParameterizedType) {
				final ParameterizedType parameterizedType = (ParameterizedType) declaredType;
				final Type[] types = parameterizedType.getActualTypeArguments();
				boolean changed = false;
				for (int i = 0; i < types.length; i++) {
					Type argumentType = types[i];
					Type trueType = getParameterizedType(ownerType, declaredClass,
							argumentType);
					if (argumentType != trueType) {
						types[i] = trueType;
						changed = true;
					}
				}
				if (changed) {
					return changedParameterizedType(parameterizedType, types);
				}
			}
			// class
			// parameterizedType
			return declaredType;

		}

		private static Type changedParameterizedType(
				final ParameterizedType parameterizedType, final Type[] changedTypes) {
			return new ParameterizedType() {
				public Type getRawType() {
					return parameterizedType.getRawType();
				}

				public Type getOwnerType() {
					return parameterizedType.getOwnerType();
				}

				public Type[] getActualTypeArguments() {
					return changedTypes;
				}
			};
		}
	}

}
