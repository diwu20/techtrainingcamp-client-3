# project-BDTC
 project-BDTC client 

### 2020.11.21 更新
1.使用RecyclerView替换了ListView
2.重写了新的Adapter，适配RecyclerView
3.由于RecyclerView没有默认的分割线，添加了新类用于自定义分割线样式
4.增加了RecyclerView中的四种点击事件，分别对应：标题、作者、日期以及整体，使用Toast反馈点击效果
5.添加了新闻正文的layout，没写Activity
#### 待解决
1.json解析
2.网络功能
3.正文文本展示对应的Activity以及文章内容的传递
4.其他功能


### 2020.11.14 更新
1.使用粗糙的方法写了要求的四种布局
2.重写Adapter，使用listView进行新闻列表的展示，根据News对象的type字段选择不同的显示布局
#### 待解决
1.ImageView和TextView的显示内容均为手动传入，没有实现对本地json文件和对图片的读取
2.进阶功能
3.listView性能有待提高
