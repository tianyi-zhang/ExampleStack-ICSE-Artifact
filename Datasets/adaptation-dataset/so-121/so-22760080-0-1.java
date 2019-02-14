public class foo {
public void foo(){
String javascript = "javascript:" +
                "setTimeout(function() {" +
                "   var iframes = document.getElementsByTagName('iframe');" +
                "   for (var i = 0, l = iframes.length; i < l; i++) {" +
                "       var iframe = iframes[i]," +
                "       a = document.createElement('a');" +
                "       a.setAttribute('href', iframe.src);" +
                "       d = document.createElement('div');" +
                "       d.style.width = iframe.offsetWidth + 'px';" +
                "       d.style.height = iframe.offsetHeight + 'px';" +
                "       d.style.top = iframe.offsetTop + 'px';" +
                "       d.style.left = iframe.offsetLeft + 'px';" +
                "       d.style.position = 'absolute';" +
                "       d.style.opacity = '0';" +
                "       d.style.filter = 'alpha(opacity=0)';" +
                "       d.style.zIndex='" + Integer.MAX_VALUE + "';" +
                "       d.style.background = 'black';" +
                "       a.appendChild(d);" +
                "       iframe.offsetParent.appendChild(a);" +
                "   }" +
                "}, 600);";
}
}