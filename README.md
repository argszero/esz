# esz

看到领导画的草图的那一刻，我就知道，这是一个ESB。和无数的其它场景一样，它是一个ESB可以解决,但又无法体现出ESB的全部能耐的场景。

放在以前，下一步就是选型->包装->深度制定->纠结的过程。现在活着的ESB产品已经不多了，选型似乎也不困难。WSO2很可能在选型中胜出，然后就是开始包装一个适合>国情的UI，然后纠结于解决各种被UI隐藏的不适合国情的问题...

但是，这次我不打算这么做了，这次，我决定：山寨一个ESB。

产生这个决定的原因还有一个，这次，我们不是卖ESB，我们不需要面对无穷无尽的未知的需求，我们有自己明确的需求域。我们只是一个项目，只是一个可能用到ESB的一
个项目。

我把这个项目起个小名叫做ESZ(大名叫啥我说了也不算)，之所以取名叫ESZ，因为山寨的精髓就是学而不像，自作主张的取其精华，去其糟粕。S和B连在一起，总是不太爽
的。替换为SZ又是啥意思呢？权且认为是"山寨"的缩写吧。


## 0. 从零开始

我们要做一个API网关，使用者可以通过我们提供的统一入口，来调用不同的API提供商提供的API。
解读一：
 1. 协议（REST）?
 2. 同步调用还是异步调用?
 3. 认证(OAUTH2)？授权？
 4. 服务等级管理（SLA）？
 5. 计费？
 6. 负载均衡和容错？
 7. API管理?版本?文档?热部署？发布和查找?监控?
 8. 二次开发？(协议适配？流程编排？）

对于每个问题，要么明确不考虑，要么就要考虑清楚。(要不我们重新谈谈包装wso2的问题？)

解读二(盈利点)：
 1. 缓存结果:程序的角度可以减少调用，节省资源。产品的角度就是减少重复付费，省钱。
 2. 二次分析:针对不同服务提供商提供的结果,或者结合我们自有数据，进行综合分析, 产生新的价值。

现在，ESZ大约应该是这样子的:

![](res/ESZ0.png)

