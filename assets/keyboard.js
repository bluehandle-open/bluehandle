//alert('start');
var originalWidth = 429;
var originalHeight = 207;
var DID = function(id) {
	return document.getElementById(id);
}
function addLoadEvent(func) {
	var oldonload= window.onload;
	if (typeof window.onload!='function') {
		window.onload=func;
	} else {
		window.onload=function(){
			oldonload();
			func();
		}
	}
}

Array.prototype.each = function(callback) {
	var len = this.length;
	for (var i=0;i<len;i++)
	{
		callback.apply(this[i],arguments);
	}			
}
function hasClassName(classNameNow,searchName) {
	return new RegExp("( ?|^)" + searchName + "\\b").test(classNameNow);
}
function addClass(element,value){
	//alert(element);
	if(!element.className){
		element.className=value;
	}else{
		newClassName=element.className;
		newClassName+=" ";
		newClassName+=value;
		element.className=newClassName;
	}
	//alert(element.innerHTML);
}

function removeClass(element,searchName) {			
	var classNameNow = element.className;
	element.className = classNameNow.replace(new RegExp("( ?|^)" + searchName + "\\b"), "");
}
/**
* @param 父级节点
* @param 需要筛选的子级节点的标签名
* @param 需要筛选的子级节点的className
*/
function getElementsByClassName(parentNode,subNodeName,className) {
	var elements = new Array();
	var children = parentNode.getElementsByTagName(subNodeName);
	var len = children.length;
	
	if (len > 0)
	{
		for (var i=0;i<len;i++)
		{
			var child = children[i];
			
			if (hasClassName(child.className,className))
			{
				elements.push(child);
			}
		}
	}
	return elements;
}
function testHandle() {
	var width = parseInt(document.documentElement.clientWidth);
	var height = parseInt(document.documentElement.clientHeight);
	this.type = 'test';
	this.getWidth = function() {
		return width;
	}
	this.getHeight = function() {
		return height;
	}
	this.press = function(buttonId) {
		DID('info').innerHTML = 'you have selected the ' + buttonId + ' button.';
	}
	this.release = function(buttonId) {
		DID('info').innerHTML = 'you have release the ' + buttonId + ' button.';
	}
}
function init(){
	var html = '';
	var contentBody = DID('contentBody');
	var handle = window.handle ? window.handle : new testHandle();
	var widthNow = handle.getWidth();//屏幕宽度
	var heightNow = handle.getHeight();//屏幕高度
	//html += 'heightNow:' + heightNow;
	var needHeight = widthNow*originalHeight/originalWidth;
	//html += 'needHeight:' + needHeight + ',';
	if (needHeight > heightNow)//屏幕高度比初始值小
	{
		//html += 'height is bigger than screen,';
		contentBody.style.height = (heightNow-10 ) + 'px';//使用屏幕高度作为容器的高度
		//html += 'contentBody height ' + contentBody.style.height ;
		contentBody.style.width = ((heightNow-10)*originalWidth/originalHeight) + 'px';//计算出等比下的容器宽度
	}
	else//屏幕高度比初始值大
	{
		contentBody.style.height = needHeight  + 'px';//使用初始高度作为容器高度
		contentBody.style.width = (widthNow-5) + 'px';//使用初始宽度作为容器宽度
	}

	//var windowWidth = document.documentElement.clientWidth;	
	//var windowHeight = document.documentElement.clientHeight;
	//var contentBody = DID('contentBody');
	var body = DID('wbody');
	
	//html += "your browser is " + windowWidth + "*" +  windowHeight + ", ";
	//html += "contentBody is " + contentBody.offsetWidth + '*' + contentBody.offsetHeight + ', ';
	//html += "body is " + body.clientWidth + '*' + body.clientHeight;

	DID('info').innerHTML  = html;
	
	getElementsByClassName(contentBody,'div','button').each(function() {
		var obj = this;
		var buttonId = obj.getAttribute('id');
		
//		obj.addEventListener('touchstart',function() {
//			addClass(obj,'clicked');
//			handle.press(buttonId);
//			return false;
//		},false);
//		obj.addEventListener('touchend',function() {
//			removeClass(obj,'clicked');
//			handle.release(buttonId);
//			return false;
//		},false);
		$(obj).bind('vmousedown',function() {
			addClass(obj,'clicked');
			handle.press(buttonId);
  		}).bind('vmouseup',function() {
  			removeClass(obj,'clicked');
			handle.release(buttonId);
  		});
		if (handle.type == 'test')
		{
			obj.onmouseover = function() {
				addClass(obj,'clicked');
				handle.press(buttonId);
			}
			obj.onmouseout = function() {
				removeClass(obj,'clicked');
				handle.release(buttonId);
			}
		}
		
	});
}
//addLoadEvent(init);
$(document).ready(function() {
	init();
});