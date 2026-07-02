#!/usr/bin/env python3
"""
匹配任务测试数据生成脚本 - 直接插入数据库

为指定用户生成 4 个场景的匹配任务及结果数据，直接写入数据库。
match_result 字段使用 JSON 格式，格式参考现有数据 (id<1009)。

用法:
  python gen_match_data.py <userId>
  python gen_match_data.py <userId> --dry-run

依赖: pip install pymysql
"""

import argparse
import json
import random
from datetime import datetime

import pymysql

# ============================================================
# 配置区 —— 按需修改
# ============================================================

DB_CONFIG = {
    "host": "39.106.165.69",
    "port": 3306,
    "user": "root",
    "password": "eto123456",
    "database": "eto_database",
    "charset": "utf8mb4",
}

TASK_CONFIG = {
    "buddy": {"status": 3, "goal": "找个周末一起玩的搭子", "remark": ""},
    "love":  {"status": 3, "goal": "认真相处，共同成长",     "remark": ""},
    "skill": {"status": 3, "goal": "互换技能，互相学习",     "remark": ""},
    "swap":  {"status": 3, "goal": "以物换物，交个朋友",     "remark": ""},
}

TAG_SOURCE_USER_ID = 293
RESULT_COUNT_MIN = 4
RESULT_COUNT_MAX = 10
SCORE_MIN = 65.0
SCORE_MAX = 98.0
TENANT_ID = 1

# ============================================================
# 模板数据 (match_result JSON 参考 id<1009 的格式)
# ============================================================

ACTIVITY_POOL = [
    "逛展拍照", "打羽毛球", "喝咖啡聊天", "看电影", "骑行",
    "剧本杀", "吃火锅", "打桌游", "跑步", "逛书店",
    "密室逃脱", "飞盘", "爬山", "唱歌", "野餐",
]

LOVE_PURPOSE_POOL = [
    "认真交友", "希望一段长期关系", "先做朋友看看",
    "结婚为目的", "共同成长", "随缘",
]

LOVE_LIFESTYLE_POOL = [
    "二次元 / 画画", "运动 / 健身", "宅家 / 看书",
    "户外 / 旅行", "音乐 / 电影", "摄影 / 手工",
]

SKILL_POOL = [
    ("Python编程", "吉他入门"), ("摄影", "日语口语"),
    ("英语口语", "钢琴"), ("视频剪辑", "咖啡拉花"),
    ("平面设计", "街舞"), ("书法", "游泳"),
    ("烹饪烘焙", "摄影"), ("数据分析", "声乐"),
    ("瑜伽", "插画"), ("办公软件技巧", "美妆护肤"),
    ("吉他 / 咖啡拉花", "AIGC"), ("烘焙 / 英语口语", "插画"),
    ("健身 / 跑步", "视频剪辑"), ("化妆 / 穿搭", "Python"),
]

SWAP_OFFER_POOL = [
    ("Kindle Paperwhite 4", "蓝牙耳机 / 机械键盘"),
    ("尤克里里", "移动硬盘 2T"),
    ("微单相机", "无人机"),
    ("机械键盘", "蓝牙音箱"),
    ("画板数绘板", "Kindle"),
    ("Switch 游戏机 + 3 张卡带", "耳机 / 机械键盘"),
    ("拍立得相机", "咖啡机"),
    ("吉他", "摄影书 + SD 卡"),
    ("天文望远镜", "智能手表"),
    ("筋膜枪", "咖啡机"),
]

TAG_POOL = ["摄影", "咖啡", "音乐", "电影", "旅行", "美食", "手工",
            "桌游", "运动", "阅读", "绘画", "编程", "宠物", "户外", "穿搭"]

NICKNAME_POOL = [
    "柠檬味的猫", "夜归人", "麦芽糖", "风一样的帅", "海边小屋",
    "铁锤", "旧物控", "相机小子", "追风少年", "月亮不睡我不睡",
    "奶茶续命中", "撸猫专业户", "打工人小李", "周末去哪儿",
    "摄影小王子", "咖啡续命", "书虫一枚", "运动达人", "手工匠人",
    "音乐发烧友", "旅行青蛙", "美食猎人", "桌游老司机", "剧本杀MVP",
    "瑜伽小熊", "画画小白", "代码诗人", "外卖品鉴师",
]

MBTI_POOL = ["INTJ", "INFP", "ENFP", "ISFJ", "ESFJ", "INFJ", "ESTP", "ISTJ", "ENFJ", "ENTP"]

RESIDENCE_POOL = [
    "杭州·西湖区", "杭州·余杭区", "杭州·滨江区", "杭州·下城区",
    "北京·海淀区", "北京·朝阳区", "上海·徐汇区", "上海·浦东新区",
    "广州·天河区", "深圳·南山区", "成都·高新区", "武汉·洪山区",
]

PROFESSION_POOL = [
    "程序员", "产品经理", "设计师", "运营", "市场", "财务",
    "教师", "医生", "学生", "自由职业", "创业者", "公务员",
]

USER_DESC_POOL = [
    "热爱生活，喜欢尝试新事物", "社恐但真诚，熟了话很多",
    "打工人一个，周末才有灵魂", "文艺青年，偶尔逗比",
    "爱看书爱旅行，生活简单", "正在努力成为更好的自己",
    "性格开朗，兴趣爱好广泛", "猫狗双全的人生赢家",
]


def pick(lst):
    return random.choice(lst)


def random_birthday():
    """生成随机出生日期 (LocalDateTime 格式)"""
    y = random.randint(1995, 2005)
    m = random.randint(1, 12)
    d = random.randint(1, 28)
    return f"{y}-{m:02d}-{d:02d} 00:00:00"


def ensure_user_basic_info(uid, cur):
    """补充被匹配用户缺失的基础信息"""
    cur.execute("""SELECT nickname, avatar, sex, birthday, residence, mbti,
                          profession, height, user_desc, wechat, location
                   FROM member_user WHERE id = %s""", (uid,))
    u = cur.fetchone()
    if not u:
        return
    fields = []
    vals = []

    # nickname (跳过 "用户" 开头的默认名)
    if not u[0] or not u[0].strip() or str(u[0]).startswith("用户"):
        fields.append("`nickname`")
        vals.append(pick(NICKNAME_POOL))
    # avatar
    if not u[1] or not u[1].strip():
        img_num = random.randint(1, 70)
        fields.append("`avatar`")
        vals.append(f"https://i.pravatar.cc/300?img={img_num}")
    # sex (0=未知/未设置)
    if u[2] is None or u[2] == 0:
        fields.append("`sex`")
        vals.append(str(random.choice([1, 2])))
    # birthday (跳过 epoch 零值 1970-01-01)
    if u[3] is None or str(u[3]).startswith("1970"):
        fields.append("`birthday`")
        vals.append(random_birthday())
    # residence
    if not u[4] or not u[4].strip():
        fields.append("`residence`")
        vals.append(pick(RESIDENCE_POOL))
    # mbti
    if not u[5] or not u[5].strip():
        fields.append("`mbti`")
        vals.append(pick(MBTI_POOL))
    # profession
    if not u[6] or not u[6].strip():
        fields.append("`profession`")
        vals.append(pick(PROFESSION_POOL))
    # height (跳过 0 默认值)
    if u[7] is None or u[7] == 0:
        fields.append("`height`")
        vals.append(str(random.randint(155, 185)))
    # user_desc
    if not u[8] or not u[8].strip():
        fields.append("`user_desc`")
        vals.append(pick(USER_DESC_POOL))
    # wechat (只填空缺，已有保留)
    if not u[9] or not u[9].strip():
        fields.append("`wechat`")
        vals.append(f"wx_user_{uid}")
    # location (residence 同值)
    if not u[10] or not u[10].strip():
        fields.append("`location`")
        vals.append(vals[fields.index("`residence`")] if "`residence`" in fields else pick(RESIDENCE_POOL))

    if fields:
        sets = ", ".join(f"{f} = '{v}'" for f, v in zip(fields, vals))
        cur.execute(f"UPDATE member_user SET {sets} WHERE id = {uid}")
        return True
    return False


def build_match_result(scene_code, user_tags, matched_tags):
    """按场景构建 match_result JSON"""
    base = {"sceneKey": scene_code}

    if scene_code == "buddy":
        activity = pick(ACTIVITY_POOL)
        base["activity"] = activity
        # 选 3 个标签
        combined = list(set((user_tags or []) + (matched_tags or [])))
        pool = combined or TAG_POOL
        tags = random.sample(pool, min(3, len(pool))) + [pick(TAG_POOL)] * max(0, 3 - len(pool))
        for i in range(3):
            base[f"tag{i+1}"] = tags[i][:20]  # 截断防止太长
        base["aiReason"] = f"你们都喜欢{tags[0]}和{tags[1]}，兴趣重合度很高，周末可以约{activity}。"

    elif scene_code == "love":
        base["mbti"] = pick(["INFP", "ISFJ", "ENFP", "INTJ", "ESFJ", "INFJ", "ESTP", "ISTJ"])
        base["height"] = random.choice([155, 158, 160, 162, 165, 168, 170, 172, 175, 178])
        base["purpose"] = pick(LOVE_PURPOSE_POOL)
        base["lifestyle"] = pick(LOVE_LIFESTYLE_POOL)
        mbti = base["mbti"]
        base["aiReason"] = f"MBTI 类型与对方高度互补，三观和生活节奏都很契合。{mbti}性格的你们容易产生共鸣。"

    elif scene_code == "skill":
        teach, want = pick(SKILL_POOL)
        base["teachSkill"] = teach
        base["wantSkill"] = want
        base["aiReason"] = f"TA 擅长{teach}，而你想学{teach}；同时 TA 想学{want}，正好你也会。双向匹配度很高！"

    elif scene_code == "swap":
        offer, want = pick(SWAP_OFFER_POOL)
        base["offer"] = offer
        base["want"] = want
        base["aiReason"] = f"TA 出{offer}，想换{want}，你们的交换意愿高度匹配。"

    return json.dumps(base, ensure_ascii=False)


def run(user_id, dry_run=False):
    conn = pymysql.connect(**DB_CONFIG)
    cur = conn.cursor()

    try:
        # 1. 场景
        cur.execute("SELECT id, scene_code FROM member_scene WHERE tenant_id = %s", (TENANT_ID,))
        scene_ids = {r[1]: r[0] for r in cur.fetchall()}
        print(f"[1/5] 场景: {scene_ids}")

        # 2. 候选用户
        cur.execute("SELECT id FROM member_user WHERE id != %s AND deleted = 0", (user_id,))
        all_ids = [r[0] for r in cur.fetchall()]
        print(f"[2/5] 候选用户: {len(all_ids)} 个")

        # 3. 来源标签
        cur.execute("SELECT DISTINCT tag_id FROM member_user_tag WHERE user_id = %s", (TAG_SOURCE_USER_ID,))
        source_tag_ids = [r[0] for r in cur.fetchall()]
        print(f"[3/5] 标签来源({TAG_SOURCE_USER_ID}): {len(source_tag_ids)} 个")

        # 4. 用户信息
        user_tags_map = {}
        for uid in all_ids:
            cur.execute("SELECT COUNT(*) FROM member_user_tag WHERE user_id = %s", (uid,))
            has_tags = cur.fetchone()[0] > 0
            cur.execute("SELECT tag_name FROM member_user_tag mut JOIN member_tag mt ON mut.tag_id = mt.id WHERE mut.user_id = %s", (uid,))
            user_tags_map[uid] = {"has_tags": has_tags, "tags": [r[0] for r in cur.fetchall()]}
        print(f"[4/5] 用户信息: {len(user_tags_map)} 个")

        if dry_run:
            print("\n[预览模式]")
            for sc, cfg in TASK_CONFIG.items():
                cnt = f"{RESULT_COUNT_MIN}-{RESULT_COUNT_MAX}" if cfg['status'] == 3 else "无"
                print(f"  {sc}: status={cfg['status']}, 结果={cnt}")
            return

        # 5. 写入
        now = datetime.now()
        tagged_users = set()

        for scene_code, cfg in TASK_CONFIG.items():
            sid = scene_ids.get(scene_code)
            status = cfg["status"]

            # 删除旧数据
            cur.execute("DELETE tr FROM member_match_task_result tr "
                        "JOIN member_match_task t ON tr.task_id = t.id "
                        "WHERE t.user_id = %s AND t.scene_id = %s", (user_id, sid))
            cur.execute("DELETE FROM member_match_task WHERE user_id = %s AND scene_id = %s", (user_id, sid))

            finish_time = now if status == 3 else None

            cur.execute(
                "INSERT INTO member_match_task "
                "(`user_id`, `scene_id`, `match_status`, `match_remark`, `match_goal`, "
                "`match_config`, `finish_time`, `creator`, `updater`, `tenant_id`, "
                "`create_time`, `update_time`) VALUES (%s, %s, %s, %s, %s, %s, %s, 'system', 'system', %s, %s, %s)",
                (user_id, sid, status, cfg["remark"], cfg["goal"], "{}", finish_time, TENANT_ID, now, now)
            )
            task_id = cur.lastrowid
            cur.connection.commit()
            print(f"  [{scene_code}] 任务: id={task_id}, status={status}")

            if status == 3:
                count = random.randint(RESULT_COUNT_MIN, RESULT_COUNT_MAX)
                chosen = random.sample(all_ids, min(count, len(all_ids))) if count <= len(all_ids) else random.choices(all_ids, k=count)

                # 补充被匹配用户基础信息
                filled = sum(1 for uid in chosen if ensure_user_basic_info(uid, cur))
                if filled:
                    cur.connection.commit()

                for matched_uid in chosen:
                    info = user_tags_map.get(matched_uid, {"tags": [], "has_tags": False})
                    score = round(random.uniform(SCORE_MIN, SCORE_MAX), 1)
                    match_result = build_match_result(scene_code, [], info["tags"])

                    cur.execute(
                        "INSERT INTO member_match_task_result "
                        "(`task_id`, `matched_user_id`, `match_score`, `match_result`, "
                        "`creator`, `updater`, `tenant_id`) VALUES (%s, %s, %s, %s, 'system', 'system', %s)",
                        (task_id, matched_uid, score, match_result, TENANT_ID)
                    )

                    # 补充标签
                    if not info["has_tags"] and matched_uid not in tagged_users:
                        for tag_id in source_tag_ids:
                            try:
                                cur.execute(
                                    "INSERT IGNORE INTO member_user_tag "
                                    "(`user_id`, `tag_id`, `source`, `create_time`) VALUES (%s, %s, 'self', NOW())",
                                    (matched_uid, tag_id)
                                )
                            except Exception:
                                pass
                        tagged_users.add(matched_uid)

                cur.connection.commit()
                print(f"  -> {len(chosen)} 条结果, 补充标签用户: {len(tagged_users)}")

        print(f"\n  OK! 全部完成")

    finally:
        cur.close()
        conn.close()


def main():
    parser = argparse.ArgumentParser(description="生成匹配任务测试数据")
    parser.add_argument("user_id", type=int, help="目标用户ID")
    parser.add_argument("--dry-run", action="store_true", help="预览")
    args = parser.parse_args()
    run(args.user_id, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
