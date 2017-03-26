package org.xidea.el;



public interface Reference{
	/**
	 * 根据传入的变量上下文,设置表达式对应的属性值
	 * @param value 新值
	 * @return 返回原值
	 */
	public Object setValue(Object value);
	public Object getValue();
	public Reference next(Object key);
	public Class<? extends Object> getType();
	public Object getBase();
	public Object getName();
}