package org.xidea.el;

import org.xidea.el.impl.ExpressionFactoryImpl;

import java.util.Map;

/**
 * @see ExpressionFactoryImpl
 */
public interface ExpressionFactory {
	/**
	 * 从中间代码或者直接的表达式文本解析成表达式对象
	 * @param el 表达式代码
	 * @return 表达式对象
	 */
	public abstract Expression create(Object el);
	/**
	 * 将表达式解析成中间状态
	 * @param el 表达式代码
	 * @return 表达式中间态(tokens)
	 */
	public abstract Object parse(String el);
	/**
	 * 添加表达式引擎内置变量
	 * @param name factory共享的变量名
	 * @param value 变量值
	 */
	public abstract void addVar(String name, Object value);
	/**
	 * 从对象(Map/JavaBean)构造一个表达式上下文
	 * @param context 传入的ctx
	 * @return 可快速访问的value stack
	 */
	public abstract Map<String,Object> wrapAsContext(Object context);
}