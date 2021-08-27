package com.example.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Article;
import com.example.domain.Comment;

/**
 * articlesテーブルを操作するリポジトリ.
 * 
 * @author nayuta
 */
@Repository
public class ArticleRepository {
	@Autowired
	private NamedParameterJdbcTemplate template;

//	// ラムダ式でRow_Mapperを定義
//	private static final RowMapper<Article> ARTICLE_ROW_MAPPER = (rs, i) -> {
//		// Articleをインスタンス化
//		Article article = new Article();
//
//		article.setId(rs.getInt("id"));
//		article.setName(rs.getString("name"));
//		article.setContent(rs.getString("content"));
//
//		return article;
//	};

	// ラムダ式でResultSetExtractorを定義
	private static final ResultSetExtractor<List<Article>> ARTICLE_RESULT_SET_EXTRACTOR = (rs) -> {
		// 記事一覧が入るarticleListを作成
		List<Article> articleList = new ArrayList<>();
		// コメント一覧が入るcommentListを作成
		List<Comment> commentList = null;

		// 前の記事IDを格納する
		int beforeId = 0;

		// while文でrsにデータがある場合は繰り返し処理を行う。
		while (rs.next()) {
			// 現在の記事IDを格納
			int nowId = rs.getInt("articleId");

			// 現在の記事IDと前の記事IDが違えば実行
			if (nowId != beforeId) {
				Article article = new Article();

				article.setId(rs.getInt("articleId"));
				article.setName(rs.getString("articleName"));
				article.setContent(rs.getString("articleContent"));

				// 空のコメントリストをarticleListに格納
				commentList = new ArrayList<Comment>();
				article.setCommentList(commentList);
				// commentListが空の状態のarticleをarticleListに格納
				articleList.add(article);
			}

			// コメントがない記事の場合は、Commentオブジェクトは生成しない
			if (rs.getInt("commentID") != 0) {
				Comment comment = new Comment();

				comment.setId(rs.getInt("commentID"));
				comment.setName(rs.getString("commentName"));
				comment.setContent(rs.getString("commentContent"));
				comment.setArticleId(rs.getInt("commentArticleId"));

				// commentListにcommentの詰まったデータを格納
				commentList.add(comment);
			}

			// 前回の記事IDを現在のIDに変更
			beforeId = nowId;
		}

		return articleList;
	};

	/**
	 * 全件検索を行うメソッド.
	 * 
	 * @return 投稿一覧
	 */
	public List<Article> findAll() {
//		// sql文作成
//		String findAllSql = "SELECT id, name, content FROM articles ORDER BY id DESC;";
//
//		// 実行
//		List<Article> articleList = template.query(findAllSql, ARTICLE_ROW_MAPPER);

		// sql文作成
		String findAllSQL = "SELECT" + "	articles.id AS articleId," + "	articles.name AS articleName,"
				+ "	articles.content AS articleContent," + "	comments.id AS commentID,"
				+ "	comments.name AS commentName," + "	comments.content AS commentContent,"
				+ "	comments.article_id AS commentArticleId" + " FROM" + "	articles" + " LEFT OUTER JOIN"
				+ "	comments" + " ON" + "	articles.id = comments.article_id" + " ORDER BY" + "	articleId DESC,"
				+ "	commentId DESC;";

		// 実行
		List<Article> articleList = template.query(findAllSQL, ARTICLE_RESULT_SET_EXTRACTOR);

		return articleList;
	}

	/**
	 * 記事を新規に投稿するメソッド.
	 * 
	 * @param article 記事情報
	 */
	public void insert(Article article) {
		// sql文の作成
		String insertArticleSql = "INSERT INTO articles(name, content) VALUES(:name, :content);";

		// :name, :contentにデータを格納
		SqlParameterSource params = new BeanPropertySqlParameterSource(article);

		// 実行
		template.update(insertArticleSql, params);
	}

	/**
	 * 記事を削除するメソッド.
	 * 
	 * @param id 削除する記事のID
	 */
	public void deleteById(int id) {
		// sql文の作成
		String deleteById = "delete from articles where id=:id;";

		// プレースホルダにデータを格納
		SqlParameterSource params = new MapSqlParameterSource("id", id);

		// 実行
		template.update(deleteById, params);
	}

}
