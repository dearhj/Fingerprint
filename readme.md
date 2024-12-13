# 指纹录入库使用方法  
### 编译  
1、将工程中android35_指纹.jar包替换到编译电脑对应的sdk下，路径platforms\android-35\android.jar  
2、删除原来的android.jar, 将android35_指纹.jar重命名为android.jar  
  
### 使用  
1、使用gradlew.publish打包aar，将生成的aar放到需要使用的工程中，直接调用对应接口接口  
  
### 注意  
1、libs/framework12.jar是暴露了系统隐藏api的jar包。  
2、android35_指纹.jar是暴露了指纹相关隐藏接口的jar包。  
3、Fingerprint-release.aar/Fingerprint-sources.jar是已经打包好的库，可以直接使用。  
  
# 参考资料  
https://blog.csdn.net/qq_38996911/article/details/129257409