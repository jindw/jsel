package org.xidea.el;

import java.util.List;

public interface ExpressionInfo{
	/**
	 * 返回表达式涉及到的变量名集合
	 * @return 表达式中使用到的变量列表
	 */
	public List<String> getVars();
	/**
	 * 返回表达式的源代码(JSON)
	 * @return 表达式字面量
	 */
	public String toString();
}