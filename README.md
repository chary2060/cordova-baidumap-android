
# 安装方式如下  

# 调用方式
```javascript
baidu_location.getCurrentPosition(function(data){
                console.log("success");
                alert(JSON.stringify(data));
               console.log(JSON.stringify(data));
  }, function(data){
      console.log("fail");
      console.log(data);
      alert(JSON.stringify(data));
  });
```
  成功返回json对象如下

