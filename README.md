# project-BDTC 
 project-BDTC client  
 最新状态以wlx分支为准

### 2020.11.27更新  
近期实现的功能：  
1. 更改活动逻辑，根据要求，主活动改为公告列表页，点击公告判断是否登录，若没有登录则进入登录活动，登录完成跳转公告内容页，解析Markdown文本并展示  
2. 利用SharedPreferences缓存TOKEN和USERNAME，APP启动时读取到ActivityCollector中的公共变量，登陆时进行缓存  
3. 实现注销登录按钮，根据当前登录情况显示/隐藏，注销登录同时清除SharedPreferences数据  
4. 主页面添加刷新按钮，用于重新加载公告数据List  
5. 使用Intent实现公告内容页点击返回回到列表页，不会重新进入登录页面  
6. 登录时，点击登录按钮关闭输入法，弹出登录提示，并在2秒后跳转至公告内容页  
7. 切换公告内容页背景颜色，并使用公共变量保存记忆背景颜色  
8. 列表页返回不会退出APP，而是回到系统界面，保持在后台运行  
9. 公告列表排序展示，时间顺序/倒序/原始顺序三种，通过ActivityCollector中的公共变量记忆排序状态
9. 修改UI  
### 待解决  
Markdown文本解析中图片的获取与展示

### 2020.11.25更新  

1. 增加了loginActivity  
2. 密码先进行加密再进行传输  
3. 将username传入MainActivity  
4. 将所有Activity变为BaseActivity的子类，由ActivityControlor统一管理  
5. Token存储在ActivityControlor的token变量下，供调用  

#### 待解决  
1. MainActivity中状态栏显示用户名  

### 2020.11.25更新

1. 增加了loginActivity
2. 将username传入MainActivity

### 2020.11.23更新2

1. 图片获取方式从本地读取更改为通过HTTP服务获取
2. 增加ScaleBitmap类，添加两种图片处理方法，分别是裁剪缩放和缩放，用于处理得到的不同大小的图片，优化内存占用
3. 微调RecyclerView对应的四种布局，以及main布局，调制app背景颜色，图标等

#### 待解决  
其他进阶功能

### 2020.11.23更新
1. 增加了GSON和OkHttp3依赖，增加了network-security-config.xml用于在高版本android中支持http  
2. 去除手动添加的测试方法，主活动中通过OkHttp从服务器获取json到本地，并使用GSON进行解析，存入List中  
3. 使用多线程，网络服务在子线程中进行，最终在主线程中更新UI  

#### 待解决  
1. 通过http服务获取图片的功能
2. 其他进阶功能

### 2020.11.21 更新
1. 使用RecyclerView替换了ListView  
2. 重写了新的Adapter，适配RecyclerView  
3. 由于RecyclerView没有默认的分割线，添加了新类用于自定义分割线样式  
4. 增加了RecyclerView中的四种点击事件，分别对应：标题、作者、日期以及整体，使用Toast反馈点击效果  
5.添 加了新闻正文的layout，没写Activity  

#### 待解决 
1. json解析  
2. 网络功能  
3. 正文文本展示对应的Activity以及文章内容的传递  
4. 其他功能  


### 2020.11.14 更新
1. 使用粗糙的方法写了要求的四种布局  
2. 重写Adapter，使用listView进行新闻列表的展示，根据News对象的type字段选择不同的显示布局  
#### 待解决  
1. ImageView和TextView的显示内容均为手动传入，没有实现对本地json文件和对图片的读取  
2. 进阶功能  
3. listView性能有待提高  
