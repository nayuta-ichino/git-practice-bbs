package com.example.repository;

//import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Comment;

/**
 * commentsテーブルを操作するリポジトリ.
 * 
 * @author nayuta
 */
@Repository
public class CommentRepository {
	@Autowired
	private NamedParameterJdbcTemplate template;

	/**
	 * コメントを新規作成するメソッド.
	 * 
	 * @param comment コメントの情報
	 */
	public void insert(Comment comment) {
		// sql文の作成
		String insertCommentSql = "INSERT INTO comments(name, content, article_id) VALUES(:name, :content, :articleId);";

		// :name, :content, :article_idにデータを格納
		SqlParameterSource params = new BeanPropertySqlParameterSource(comment);

		// 実行
		template.update(insertCommentSql, params);
	}

	/**
	 * コメントを削除するメソッド.
	 * 
	 * @param articleId 付属の記事ID
	 */
	public void deleteByArticleId(int articleId) {
		// sql文作成
		String deleteByArticleIdSql = "delete from comments where article_id=:articleId;";

		// :articleIdに実数を格納
		SqlParameterSource params = new MapSqlParameterSource().addValue("articleId", articleId);

		// 実行
		template.update(deleteByArticleIdSql, params);
	}

}
