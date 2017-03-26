package org.xidea.el;


public interface ReferenceExpression{
	/**
	 * 根据传入的变量上下文，执行表达式
	 * @see org.xidea.el.impl.ExpressionImpl#prepare(Object context)
	 * @param context 变量表
	 * @return 返回值引用
	 */
	public Reference prepare(Object context);
	public String toString();
}