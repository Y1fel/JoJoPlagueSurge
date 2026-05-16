JoJoPlagueSurge 1.0.4
==============================

项目类型
--------
Minecraft Forge 1.20.1 模组。

基础信息
--------
- Mod ID: jojoplaguesurge
- Mod Name: JoJoPlagueSurge
- Version: 1.0.4
- Author: Y1fel
- License: MIT
- Java: 17
- Minecraft: 1.20.1
- Forge: 47.4.10
- Mappings: Parchment 2023.09.03-1.20.1

简介
----
JoJoPlagueSurge 是一个以 JoJo 为主题的 Forge 模组，目前主要包含替身召唤、技能 HUD、杜比华 / 蓝色夏威夷技能逻辑、OZONE 宝贝区域效果、杜王町相关实体、特殊药水和若干测试能力。

模组的核心玩法由服务端技能逻辑、客户端按键、JCraft 风格 HUD、GeckoLib 实体模型动画和少量 Mixin 共同组成。

主要内容
--------
- 替身系统：
  - 杜比华 Doobie Wah
  - 蓝色夏威夷 Blue Hawaii
  - 替身会作为玩家乘客跟随在玩家身后左侧
  - 替身不可攻击、不可碰撞、不可被玩家触碰造成异常伤害

- 技能 HUD：
  - 使用 JCraft Ability HUD 的边框、图标和冷却遮罩
  - 会显示玩家实际绑定的按键，而不是写死的 Z/X/C 文本
  - 根据当前玩家状态显示杜比华、蓝色夏威夷或 OZONE 技能栏

- 方块：
  - OZONE 宝贝 / Ozone Babe
  - 放置后记录拥有者
  - 右键启动区域效果
  - 潜行右键或再次右键当前激活的 OZONE 可回收
  - 回收后进入 30 分钟房屋效果冷却

- 实体：
  - 杜王町居民 Morioh Villager
  - 杜王町罪犯 Morioh Criminal
  - 奇迹于你 / Wonder of U
  - 石虫 Stone Insect
  - 追踪飓风 Tracking Tornado

- 物品：
  - 带血的牙齿 Bloody Tooth
  - 杜比华召唤光碟
  - 蓝色夏威夷召唤光碟
  - 新月光盘 Crescent Moon Disc
  - 灵视药水 Spirit Vision Potion
  - 神秘的药水 Mysterious Potion
  - 杜王町居民、杜王町罪犯、奇迹于你、石虫刷怪蛋

- 创造模式栏：
  - 独立 JoJo 模组栏，包含本模组主要物品、方块和刷怪蛋

默认按键
--------
- Z: 一技能
- X: 二技能
- C: 三技能
- N: 召唤或收回当前拥有的替身
- U: Step Up 测试能力
- Z: 新月技能默认也绑定在 Z，可能与一技能冲突，可在按键设置中自行调整

替身与光碟
----------
- 使用杜比华召唤光碟：
  - 获得或撤销杜比华所有权
  - 获得所有权时会同时召唤杜比华

- 使用蓝色夏威夷召唤光碟：
  - 获得或撤销蓝色夏威夷所有权
  - 获得所有权时会同时召唤蓝色夏威夷

- 按 N：
  - 如果玩家拥有替身，则召唤或收回当前拥有的替身
  - 当前只支持杜比华与蓝色夏威夷两种替身所有权

杜比华技能
----------
杜比华拥有 2 个可见技能槽。

- 一技能：追踪飓风
  - 默认按 Z
  - 冷却 10 秒
  - 需要目标带有 dbh_target 标签
  - 测试配置开启后，可直接锁定 32 格内最近的任意生物目标
  - 从已召唤的杜比华位置生成 Tracking Tornado
  - 飓风会追踪目标，命中后造成魔法伤害并附加短暂缓慢

- 二技能：飓风屏障
  - 默认按 X
  - 冷却 55 秒
  - 给玩家附加：
    - more_potion_effects:solid_shield，15 秒，如果该效果存在
    - 抗性提升 II，15 秒
    - 生命恢复 I，5 秒
  - 释放时生成云粒子

蓝色夏威夷技能
--------------
蓝色夏威夷拥有 3 个可见技能槽。

- 一技能：牙齿标记
  - 默认按 Z
  - 正常玩法下需要通过带血的牙齿进行标记
  - 测试配置开启后，可直接锁定 32 格内最近的实体目标

- 二技能：发动追猎
  - 默认按 X
  - 需要玩家已经召唤蓝色夏威夷
  - 需要已有锁定目标
  - 启动后玩家会被固定在发动位置
  - 给发动者持续发光，并尽量附加 More Potion Effects 的 imprison / imprision 效果
  - 在玩家附近召唤 20 个杜王町居民，并引导它们追击目标

- 三技能：解除追猎
  - 默认按 C
  - 解除追猎状态
  - 清理由带血牙齿触发的标记和锁定数据
  - 进入 20 分钟冷却
  - 冷却结束后会返还一颗带血的牙齿

带血的牙齿机制
--------------
- 非蓝色夏威夷持有者首次获得带血的牙齿时，会被记录为牙齿标记目标
- 所有拥有蓝色夏威夷替身的玩家会锁定该目标
- 被标记者即使之后丢掉牙齿，标记状态仍会保留，直到蓝色夏威夷三技能清理

OZONE 宝贝技能
--------------
携带 OZONE 宝贝物品时会显示 OZONE HUD。OZONE 当前有 2 个直接技能槽，以及 1 个由放置方块启动的房屋区域效果。

- 一技能：
  - 默认按 Z
  - 以释放者为中心，检查 16 格半径内其他非旁观玩家
  - 持续 60 秒，结束后进入 60 秒冷却
  - 立即给目标附加 more_potion_effects:heavy I，持续 60 秒
  - 10 秒后附加 more_potion_effects:injury_outburst I，持续 50 秒
  - 45 秒后附加原版 Darkness，持续到技能结束

- 二技能：
  - 默认按 X
  - 以释放者为中心，检查 16 格半径内其他非旁观玩家
  - 持续 6 秒，结束后进入 30 秒冷却
  - 给目标附加 imprison / imprision 效果 5 秒
  - 如果 More Potion Effects 不存在对应效果，会用高等级缓慢和跳跃效果作为后备禁锢

- OZONE 房屋区域效果：
  - 放置 OZONE 后右键启动
  - 只允许拥有者启动或回收
  - 影响以方块为中心 24 格半径内的生物，不包括释放者和替身
  - 进入区域后：
    - 立即刷新 heavy I
    - 10 秒后刷新 heavy II
    - 15 秒后附加 injury_outburst I，持续 30 秒
    - 25 秒后刷新 Darkness
    - 150 秒后刷新 imprison / imprision
  - 离开区域、方块被移除或效果结束时会清理相关标签和 injury_outburst 标记

新月光盘
--------
- 使用新月光盘可切换新月技能使用权
- 新月技能默认按 Z
- 技能范围为玩家周围 5 格内其他非旁观玩家
- 命中目标时尝试附加：
  - more_potion_effects:heavy，5 秒
  - more_potion_effects:bleeding，1 秒
- 冷却 8 秒

药水与效果
----------
- 灵视药水：
  - 饮用后获得本模组 spirit_vision 效果，持续 3 分钟
  - 客户端 Mixin 会让 256 格内的杜王町罪犯显示发光轮廓

- 神秘的药水：
  - 测试物品
  - 饮用后模拟 OZONE 一技能节奏：
    - 立即 heavy I，60 秒
    - 10 秒后 injury_outburst I，50 秒
    - 45 秒后 bleeding I，持续到结束
  - Tooltip 为蓝色斜体

- Step Up 测试键：
  - 默认按 U
  - 尝试给玩家附加 more_potion_effects:step_up III，持续 2 秒
  - 冷却 30 秒

实体说明
--------
- 杜王町居民：
  - 10 点生命
  - 1 颗心攻击伤害
  - 256 格跟随范围
  - 不会因为距离玩家过远而自然移除
  - 有 10 种随机外观

- 杜王町罪犯：
  - 继承杜王町居民
  - 使用同一套随机外观
  - 自动加入红色队伍以获得红色轮廓显示
  - 死亡时尝试掉落 jcraft:sinners_soul

- 奇迹于你：
  - 40 点生命
  - 4 颗心攻击伤害
  - 会主动攻击玩家

- 石虫：
  - 12 点生命
  - 1 颗心攻击伤害
  - 会反击伤害来源

- 追踪飓风：
  - 由杜比华一技能生成
  - 最多存在 15 秒
  - 会追踪锁定目标
  - 命中后造成 2 点魔法伤害并附加缓慢 III，2 秒

配置项
------
通用配置位于 Forge common config。

- duwangSkill1AllowAnyLivingTargetForTest
  - 默认 false
  - true 时杜比华一技能可锁定 32 格内最近任意生物，便于测试

- blueHawaiiSkill1AllowAnyEntityTargetForTest
  - 默认 false
  - true 时蓝色夏威夷一技能可直接锁定 32 格内最近实体，便于测试

- logDirtBlock、magicNumber、magicNumberIntroduction、items
  - Forge MDK 示例配置项，目前主要用于开发日志演示

依赖
----
mods.toml 中声明的强制运行依赖：
- Forge
- Minecraft 1.20.1
- GeckoLib 4.4.9+
- JCraft 0.17.6+

Gradle 开发环境中还引入：
- MCLib 20
- Architectury API
- Trimmed
- AzureLib
- Cloth Config
- Player Animator
- TerraBlender

可选联动：
- More Potion Effects
  - 代码通过运行时注册表查找 heavy、bleeding、injury_outburst、solid_shield、step_up、imprison / imprision 等效果
  - 如果缺少部分效果，对应能力会跳过或使用有限后备逻辑

项目结构
--------
- src/main/java/com/Y1fel/JoJoPlagueSurge
  - 主模组入口、配置、注册逻辑

- src/main/java/com/Y1fel/JoJoPlagueSurge/entity
  - 实体注册、自定义实体、模型和渲染器

- src/main/java/com/Y1fel/JoJoPlagueSurge/item
  - 物品、光碟、药水和创造模式栏

- src/main/java/com/Y1fel/JoJoPlagueSurge/block
  - OZONE 方块注册与交互逻辑

- src/main/java/com/Y1fel/JoJoPlagueSurge/network
  - C2S / S2C 数据包、技能释放和冷却同步

- src/main/java/com/Y1fel/JoJoPlagueSurge/client
  - 按键绑定、HUD 渲染、客户端冷却状态

- src/main/java/com/Y1fel/JoJoPlagueSurge/skill
  - 技能 ID、图标路径和技能栏定义

- src/main/java/com/Y1fel/JoJoPlagueSurge/mixin
  - 替身乘客位置修正
  - 灵视高亮杜王町罪犯

- src/main/resources/assets/jojoplaguesurge
  - 语言文件、模型、贴图、动画、物品模型、方块状态

- src/main/resources/data/jojoplaguesurge
  - Loot table 等数据资源

开发运行
--------
- 推荐使用 IntelliJ IDEA 导入 Gradle 项目
- 生成 IntelliJ 运行配置：
  gradlew genIntellijRuns
- 客户端运行目录：
  run/
- 配置文件目录：
  run/config/

注意事项
--------
- 当前技能键位存在复用：Z 同时是通用一技能和新月技能默认键，实际游玩建议在设置里调整。
- 部分中文服务端提示字符串在源码中存在编码显示问题，但语言文件 zh_cn.json / en_us.json 是正常的。
- OZONE 房屋拥有者数据目前保存在运行时内存映射中，服务器重启后放置方块的拥有者关系不会从方块数据恢复。
- More Potion Effects 未作为 Gradle 强制依赖启用，相关效果需要运行时存在才会真正生效。
