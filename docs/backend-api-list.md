# 后端接口列表

> 整理日期：2026-06-14
>
> 范围：当前项目 AI 社交业务相关 App/Admin 接口，主要来自 `yudao-module-member`。通用若依/芋道平台接口、商城接口和 MES 接口仍以 Swagger/OpenAPI 与各模块 Controller 为准。

## 通用约定

| 项 | 说明 |
|---|---|
| App 基础前缀 | `/app-api`，代码中的 Controller 路径如 `/member/social/scene/list` |
| Admin 基础前缀 | `/admin-api`，代码中的 Controller 路径如 `/member/social/scene/page` |
| 返回格式 | `CommonResult<T>`，一般为 `{ code, data, msg }` |
| 鉴权 | App 端大多需要登录态；`@PermitAll` 接口除外 |
| 分页 | 入参通常为 `pageNo`、`pageSize`，返回 `PageResult<T>{ list, total }` |
| 详细字段 | 见 `docs/api.md` 与对应 `*ReqVO`、`*RespVO` |

## 一、App 端接口

### 1. 认证登录

源码：`yudao-module-member/.../controller/app/auth/AppAuthController.java`

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/member/auth/login` | 手机号 + 密码登录 | 免登录 |
| POST | `/member/auth/logout` | 登出 | 免登录 |
| POST | `/member/auth/refresh-token` | 刷新令牌，参数 `refreshToken` | 免登录 |
| POST | `/member/auth/sms-login` | 手机号 + 验证码登录 | 免登录 |
| POST | `/member/auth/send-sms-code` | 发送手机验证码 | 免登录 |
| POST | `/member/auth/validate-sms-code` | 校验手机验证码 | 免登录 |
| GET | `/member/auth/social-auth-redirect` | 获取社交授权跳转地址，参数 `type`、`redirectUri` | 免登录 |
| POST | `/member/auth/social-login` | 社交快捷登录 | 免登录 |
| POST | `/member/auth/weixin-mini-app-login` | 微信小程序一键登录 | 免登录 |
| POST | `/member/auth/create-weixin-jsapi-signature` | 创建微信 JS SDK 签名，参数 `url` | 免登录 |

### 2. 用户与分身

源码：`yudao-module-member/.../controller/app/user/AppMemberUserController.java`

| 方法 | 路径 | 说明 | 前端封装 |
|---|---|---|---|
| GET | `/member/user/get` | 获取会员基本信息 | 框架会员接口 |
| PUT | `/member/user/update` | 修改会员基本信息 | 框架会员接口 |
| PUT | `/member/user/update-mobile` | 修改手机号 | 框架会员接口 |
| PUT | `/member/user/update-mobile-by-weixin` | 基于微信小程序授权码修改手机号 | 框架会员接口 |
| PUT | `/member/user/update-password` | 修改密码 | 框架会员接口 |
| PUT | `/member/user/reset-password` | 重置密码 | 框架会员接口 |
| POST | `/member/user/clone` | 创建 AI 社交分身 | `sheep/api/agent/profile.js#createClone` |
| GET | `/member/user/clone/info` | 获取分身信息 | `sheep/api/agent/profile.js#getCloneInfo` |
| PUT | `/member/user/clone/update` | 更新分身信息 | `sheep/api/agent/profile.js#updateClone` |
| POST | `/member/user/nickname/generate` | 生成随机昵称 | `sheep/api/agent/profile.js#generateNickname` |
| GET | `/member/user/avatar/random` | 随机获取头像 | `sheep/api/agent/profile.js#getRandomAvatar` |

创建分身推荐初始化顺序：

| 顺序 | 接口 | 用途 |
|---|---|---|
| 1 | `POST /member/user/nickname/generate` | 获取随机昵称 |
| 2 | `GET /member/user/avatar/random` | 获取头像预览，可选 |
| 3 | `GET /member/social/region/tree` | 常住地选择器 |
| 4 | `GET /member/social/tag/list?category=profession` | 职业标签 |
| 5 | `GET /member/social/school/search?name=xxx` | 学校搜索 |
| 6 | `POST /member/user/clone` | 提交分身信息 |

### 3. 社交基础数据

源码：`yudao-module-member/.../controller/app/social/AppSocialController.java`

| 方法 | 路径 | 说明 | 前端封装 |
|---|---|---|---|
| GET | `/member/social/scene/list` | 获取用户可见场景列表 | `sheep/api/agent/scene.js#getSceneList` |
| GET | `/member/social/scene/form` | 获取场景表单，参数 `sceneCode` | `sheep/api/agent/scene.js#getSceneForm` |
| GET | `/member/social/school/search` | 搜索学校，参数 `name` | `sheep/api/agent/school.js#searchSchool` |
| POST | `/member/social/school/verify` | 提交学校认证申请 | `sheep/api/agent/school.js#submitSchoolVerify` |
| GET | `/member/social/school/verify/status` | 查询学校认证状态 | `sheep/api/agent/school.js#getSchoolVerifyStatus` |
| GET | `/member/social/tag/list` | 获取预置标签列表，可选参数 `category` | `sheep/api/agent/tag.js#getTagList` |
| GET | `/member/social/tag/children` | 获取指定分类下子标签，参数 `category` | `sheep/api/agent/tag.js#getTagChildren` |
| GET | `/member/social/region/tree` | 获取省市区树 | `sheep/api/agent/profile.js#getRegionTree` |
| POST | `/member/social/user-tag/batch-save` | 覆盖式批量保存用户标签 | `sheep/api/agent/tag.js#batchSaveUserTags` |
| GET | `/member/social/user-tag/list` | 获取当前用户标签 ID 列表 | `sheep/api/agent/tag.js#getUserTags` |
| GET | `/member/social/user/detail` | 获取用户社交主页，参数 `userId` | `sheep/api/agent/match.js#getUserDetail` |

### 4. 匹配

源码：`yudao-module-member/.../controller/app/social/AppSocialController.java`

| 方法 | 路径 | 说明 | 前端封装 |
|---|---|---|---|
| POST | `/member/social/match/create` | 创建匹配任务 | `sheep/api/agent/scene.js#createMatchTask` |
| GET | `/member/social/match/task/page` | 分页查询匹配记录 | `sheep/api/agent/scene.js#getMatchTaskPage` |
| GET | `/member/social/match/scene/status` | 获取指定场景匹配状态，参数 `sceneId` | `sheep/api/agent/scene.js#getSceneMatchStatus` |
| POST | `/member/social/match/config/save` | 保存场景匹配配置 | `sheep/api/agent/scene.js#saveMatchConfig` |
| GET | `/member/social/match/config/get` | 获取上次额外字段，参数 `sceneCode` | `sheep/api/agent/scene.js#getMatchConfig` |

常用请求字段：

| 接口 | 关键字段 |
|---|---|
| `/match/create` | `sceneId`、`tagIds`、`matchGoal`、`matchRemark`、`extraFields` |
| `/match/config/save` | `sceneCode`、`tagIds`、`extraFields` |
| `/match/task/page` | `pageNo`、`pageSize` |

### 5. 社交账号绑定

源码：`yudao-module-member/.../controller/app/social/AppSocialUserController.java`

| 方法 | 路径 | 说明 | 前端封装 |
|---|---|---|---|
| POST | `/member/social-user/bind` | 社交绑定，使用 code 授权码 | `sheep/api/member/social.js#socialBind` |
| DELETE | `/member/social-user/unbind` | 取消社交绑定 | `sheep/api/member/social.js#socialUnbind` |
| GET | `/member/social-user/get` | 获取社交用户，参数 `type` | `sheep/api/member/social.js#getSocialUser` |
| POST | `/member/social-user/wxa-qrcode` | 获取微信小程序码 base64 | `sheep/api/member/social.js#getWxaQrcode` |
| GET | `/member/social-user/get-subscribe-template-list` | 获取微信小程序订阅模板列表 | `sheep/api/member/social.js#getSubscribeTemplateList` |

## 二、Admin 端接口

### 1. AI 社交场景管理

源码：`yudao-module-member/.../controller/admin/social/SceneController.java`

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| POST | `/member/social/scene/create` | 创建场景 | `member:social:scene:create` |
| PUT | `/member/social/scene/update` | 更新场景 | `member:social:scene:update` |
| DELETE | `/member/social/scene/delete` | 删除场景，参数 `id` | `member:social:scene:delete` |
| GET | `/member/social/scene/get` | 获取场景详情，参数 `id` | `member:social:scene:query` |
| GET | `/member/social/scene/page` | 分页查询场景 | `member:social:scene:query` |

### 2. 学校认证审核

源码：`yudao-module-member/.../controller/admin/social/SchoolVerifyController.java`

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/member/social/school/verify/page` | 分页查询学校认证 | `member:social:school:verify:query` |
| PUT | `/member/social/school/verify/audit` | 审核学校认证 | `member:social:school:verify:audit` |

### 3. 会员基础管理

源码：`yudao-module-member/.../controller/admin`

| 模块 | 路径前缀 | 主要接口 |
|---|---|---|
| 会员用户 | `/member/user` | `PUT /update`、`PUT /update-level`、`PUT /update-point`、`GET /get`、`GET /page` |
| 会员配置 | `/member/config` | `PUT /save`、`GET /get` |
| 用户分组 | `/member/group` | `POST /create`、`PUT /update`、`DELETE /delete`、`GET /get`、`GET /list-all-simple`、`GET /page` |
| 会员标签 | `/member/tag` | `POST /create`、`PUT /update`、`DELETE /delete`、`GET /get`、`GET /list-all-simple`、`GET /list`、`GET /page` |
| 会员等级 | `/member/level` | `POST /create`、`PUT /update`、`DELETE /delete`、`GET /get`、`GET /list-all-simple`、`GET /list` |
| 签到规则 | `/member/sign-in/config` | `POST /create`、`PUT /update`、`DELETE /delete`、`GET /get`、`GET /list` |
| 签到记录 | `/member/sign-in/record` | `GET /page` |
| 积分记录 | `/member/point/record` | `GET /page` |
| 经验记录 | `/member/experience-record` | `GET /get`、`GET /page` |
| 等级记录 | `/member/level-record` | `GET /get`、`GET /page` |
| 收件地址 | `/member/address` | `GET /list` |

## 三、当前前端使用情况

来源：`yudao-mall-uniapp/sheep/api/agent/*.js` 与 `docs/build/frontend-backend-api-alignment.md`。

| 状态 | 接口 |
|---|---|
| 已使用 | `/member/user/clone`、`/member/user/clone/info`、`/member/user/clone/update`、`/member/user/nickname/generate`、`/member/social/region/tree`、`/member/social/tag/list`、`/member/social/school/search`、`/member/social/scene/list`、`/member/social/scene/form`、`/member/social/match/create`、`/member/social/match/scene/status`、`/member/social/user/detail` |
| 已封装未使用/预留 | `/member/user/avatar/random`、`/member/social/school/verify`、`/member/social/school/verify/status`、`/member/social/tag/children`、`/member/social/user-tag/batch-save`、`/member/social/user-tag/list`、`/member/social/match/task/page`、`/member/social/match/config/save`、`/member/social/match/config/get` |

## 四、待确认与注意点

| 项 | 说明 |
|---|---|
| `sceneCode` 类型 | 当前后端 `/member/social/scene/form`、`/member/social/match/config/*` 接收 `String sceneCode`，前端传 `buddy/love/skill/swap`；`scene/list` 响应文档里曾写成 Long，需要以当前 VO/数据库实际字段为准。 |
| 更新分身路径 | 当前源码为 `PUT /member/user/clone/update`，前端封装也使用该路径；旧文档中出现过 `PUT /member/user/clone`，已不作为当前路径使用。 |
| 认证状态查询实现 | `/school/verify/status` 当前通过 `searchSchool("")` 再过滤学校名称；而 `searchSchool("")` 返回空列表时可能拿不到 `schoolName`，但不影响 `verifyStatus`。 |
| 匹配配置读取 | `/match/config/get` 当前实现返回空 Map，后续若要回填 `extraFields`，需要补充从最近任务 `match_config` 读取。 |
| 未实现设计接口 | 旧 S2 设计中的 `/member/social/tag/tree`、`/member/social/admin/tag/*`、`/member/social/admin/section/*` 当前未在 Controller 中实现。 |

