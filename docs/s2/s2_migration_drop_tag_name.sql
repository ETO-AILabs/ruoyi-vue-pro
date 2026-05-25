-- ============================================================================
-- 迁移脚本：member_tag 仅保留 tag_name，删除 name 列
-- 适用：之前同时存在 name / tag_name 两列的环境
-- 执行前请先备份 member_tag 表
-- ============================================================================

-- 1. 回填：把 name 写入空的 tag_name
UPDATE member_tag
SET tag_name = name
WHERE (tag_name IS NULL OR tag_name = '')
  AND name IS NOT NULL;

-- 2. 删除 name 列
ALTER TABLE member_tag DROP COLUMN name;
