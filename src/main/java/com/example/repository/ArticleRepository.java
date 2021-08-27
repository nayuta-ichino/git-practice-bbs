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
 * articlesテーブルを操作するリポジトリ(Dao).
 * 
 * @author okahikari
 *
 */
@Repository
public class ArticleRepository {
	
	@Autowired
	private NamedParameterJdbcTemplate template;
	
	private static final ResultSetExtractor<List<Article>> ALTICLE_ROW_MAPPER
	= (rs) -> {
		List<Article> articleList = new ArrayList<>();
		List<Comment> commentList = null;
		int idNumber = 0;
		while (rs.next()) {
			int nowIdNumber = rs.getInt("id");
			if(idNumber != nowIdNumber) {
				Article article = new Article();
				commentList = new ArrayList<>();
				article.setId(rs.getInt("id"));
				article.setName(rs.getString("name"));
				article.setContent(rs.getString("content"));
				article.setCommentList(commentList);
				articleList.add(article);
			}
			if (rs.getInt("com_id") != 0) {
				Comment comment = new Comment();
				comment.setId(rs.getInt("com_id"));
				comment.setName(rs.getString("com_name"));
				comment.setContent(rs.getString("com_content"));
				comment.setArticleId(rs.getInt("article_id"));
				commentList.add(comment);
			}
			idNumber = nowIdNumber;
		}
		return articleList;
	};
	
	/**
	 * 全記事情報を取得する.
	 * (記事が存在しない場合はサイズ0件の記事一覧を返す)。
	 * @return 全記事情報
	 */
	public List<Article> findAll(){
		String sql ="SELECT a.id, a.name, a.content, c.id AS com_id, c.name AS com_name, c.content AS com_content, c.article_id FROM articles AS a LEFT OUTER JOIN comments AS c on a.id = c.article_id ORDER BY a.id DESC, c.id ASC;";
		List<Article> articleList = template.query(sql, ALTICLE_ROW_MAPPER);
		return articleList;
	}
	
	/**
	 * 渡した記事情報を保存する.
	 * @param article 記事情報
	 */
	public void insert(Article article) {
		SqlParameterSource param
		= new BeanPropertySqlParameterSource(article);
		String sql = "INSERT INTO articles(name, content) VALUES (:name, :content);";
		template.update(sql, param);
	}
	
	/**
	 * 渡された記事情報を削除する.
	 * (記事情報に関連するコメントも削除する)
	 * @param id 記事ID
	 */
	public void deleteById(int id) {
		String sql = "DELETE FROM articles WHERE id = :id;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		template.update(sql, param);
	}
}