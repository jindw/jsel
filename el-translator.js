/*
 * List Template
 * License LGPL(您可以在任何地方免费使用,但请不要吝啬您对框架本身的改进)
 * http://www.xidea.org/project/lite/
 * @author jindw
 * @version $Id: template.js,v 1.4 2008/02/28 14:39:06 jindw Exp $
 */

var NUMBER_CALL = /^(\d+)(\.\w+)$/;//10.0.toString(2), 10.toString(2)
//var PRESERVED = /^(break|case|catch|continue|default|delete|do|else|finally|for|function|if|in|instanceof|new|return|switch|throw|try|typeof|var|void|while|with|class|const|debugger|enum|export|extends|import|super)$/


/**
 * 将某一个token转化为表达式
 */
function stringifyJSEL(el,context){
	var type = el[0];
	if(type<=0){//value
		return stringifyValue(el,context)
	}else if(getTokenParamIndex(type) ==3){//两个操作数
		return stringifyInfix(el,context);
	}else{
		return stringifyPrefix(el,context);
	}
	
}
/**
 * 翻译常量字面量
 */
function stringifyValue(el,context){
	var param = el[1];
	switch(el[0]){
    case VALUE_CONSTANTS:
        return (param && param['class']=='RegExp' && param.literal) || JSON.stringify(param);
    case VALUE_VAR:
    	//console.log(PRESERVED.test(param),param)
    	if(param == 'for'){
    		var f = context.getForName();
    		if(f){
    			param = f;
    		}
    	//}else if(PRESERVED.test(param)){
    	//	return param+'__';
    	}else{
    		
    	}
    	return context.getVarName(param) ;
    case VALUE_LIST:
    	return "[]";
    case VALUE_MAP:
    	return "{}";
	}
}

function stringifyGetter(context,el){
	var el1 = el[1];
	var el2 = el[2];
	if(el2[0] == VALUE_CONSTANTS && el1[0] == VALUE_VAR){
		var varName = getTokenParam(el1)
		var propertyName = getTokenParam(el2)
		if(typeof propertyName == 'string'){
			var forAttr = context.findForAttribute(varName,propertyName);
			if(forAttr){
				return forAttr;
			}
		}
	}
	var value1 = stringifyJSEL(el1,context);
	var value2 = stringifyJSEL(el2,context);
	//safe check
	//return __get__(value1,value2)
	//default impl(without safy check)
	value1 = addELQute(el,el1,value1)
	return context.genGetCode(value1,value2);
}
function stringifyPropertyCall(context,propertyEL,callArguments){
	var value1 = stringifyGetter(context,propertyEL);
	var value2 = stringifyJSEL(callArguments,context);
	if(value1.match(/\)$/)){
		//safe property call
		return value1.slice(0,-1)+','+value2+')'
	}else{
		value1 = value1.replace(NUMBER_CALL,'($1)$2')//void 10.toString(2) error!!
		return value1+"("+value2.slice(1,-1)+')';
	}
}
/**
 * 翻译中缀运算符
 */
function stringifyInfix(el,context){
	var type = el[0];
	var el1 = el[1];
	var el2 = el[2];
	if(type == OP_GET){
		return stringifyGetter(context,el);
	}else if(type == OP_INVOKE && el1[0] == OP_GET){
		return stringifyPropertyCall(context,el1,el2);
	}
	var opc = findTokenText(el[0]);
	var value1 = stringifyJSEL(el1,context);
	var value2 = stringifyJSEL(el2,context);
	//value1 = addELQute(el,el[1],value1);
	switch(type){
	case OP_INVOKE:
		value2 = value2.slice(1,-1);
		value1 = value1.replace(NUMBER_CALL,'($1)$2')
		return value1+"("+value2+')';
	//case OP_GET:
		
	case OP_JOIN:
		if("[]"==value1){
			return "["+value2+"]"
		}else{
			return value1.slice(0,-1)+','+value2+"]"
		}
	case OP_PUT:
		value2 = JSON.stringify(getTokenParam(el))+":"+value2+"}";
		if("{}"==value1){
			return "{"+value2
		}else{
			return value1.slice(0,-1)+','+value2
		}
    case OP_QUESTION:
    	//1?2:3 => [QUESTION_SELECT,
    	// 					[QUESTION,[CONSTANTS,1],[CONSTANTS,2]],
    	// 					[CONSTANTS,3]
    	// 			]
    	//throw new Error("表达式异常：QUESTION 指令翻译中应该被QUESTION_SELECT跳过");
    	return null;//前面有一个尝试，此处应返回null，而不是抛出异常。
    case OP_QUESTION_SELECT:
    /**
 ${a?b:c}
 ${a?b1?b2:b3:c}
 ${222+2|a?b1?b2:b3:c}
     */
     	//?:已经是最低优先级了,无需qute,而且javascript 递归?: 也无需优先级控制
    	var test = stringifyJSEL(el1[1],context);
    	var value1 = stringifyJSEL(el1[2],context);
    	return test+'?'+value1+":"+value2;
	}
	value1 = addELQute(el,el1,value1)
	value2 = addELQute(el,el2,null,value2)
	return value1 + opc + value2;
}
/**
 * 翻译前缀运算符
 */
function stringifyPrefix(el,context){
	var type = el[0];
	var el1 = el[1];
	var value = stringifyJSEL(el1,context);
	var param = getTokenParam(el);
	value = addELQute(el,el1,null,value)
    var opc = findTokenText(type);
	return opc+value;
}

if(typeof require == 'function'){
exports.stringifyJSEL=stringifyJSEL;
var getTokenParam=require('./expression-token').getTokenParam;
var getTokenParamIndex=require('./expression-token').getTokenParamIndex;
var findTokenText=require('./expression-token').findTokenText;
var addELQute=require('./expression-token').addELQute;
var OP_GET=require('./expression-token').OP_GET;
var OP_IN=require('./expression-token').OP_IN;
var OP_INVOKE=require('./expression-token').OP_INVOKE;
var OP_JOIN=require('./expression-token').OP_JOIN;
var OP_PUT=require('./expression-token').OP_PUT;
var OP_QUESTION=require('./expression-token').OP_QUESTION;
var OP_QUESTION_SELECT=require('./expression-token').OP_QUESTION_SELECT;
var VALUE_CONSTANTS=require('./expression-token').VALUE_CONSTANTS;
var VALUE_LIST=require('./expression-token').VALUE_LIST;
var VALUE_MAP=require('./expression-token').VALUE_MAP;
var VALUE_VAR=require('./expression-token').VALUE_VAR;
}