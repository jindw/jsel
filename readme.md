# XMLDOM [![Build Status](https://secure.travis-ci.org/bigeasy/xmldom.png?branch=master)](http://travis-ci.org/bigeasy/xmldom) [![Coverage Status](https://coveralls.io/repos/bigeasy/xmldom/badge.png?branch=master)](https://coveralls.io/r/bigeasy/xmldom) [![NPM version](https://badge.fury.io/js/xmldom.png)](http://badge.fury.io/js/xmldom)

A JavaScript implementation of JavaScript Style Expression Engine.

Install:
-------
>npm install js-el

Example:
====
```javascript
var Expression = require('js-el');
var el = new Expression("(object.attr+x)+1+JSON.stringfy([c,d])")

console.info(String(el))
console.info(el.getVarMap())
console.info(el.getCallMap())
```

```javascript
object.attr+x+1+JSON.stringfy([c,d])
{ object: [ 'attr' ],
  x: [ '' ],
  JSON: [ 'stringfy' ],
  c: [ '' ],
  d: [ '' ] }
{ JSON: [ 'stringfy' ] }
```
API Reference
=====

 * [Expression]:
 	Constructor
 	#evaluate(token)
 	#getVarMap()
 	#getCallMap()
 	#token
