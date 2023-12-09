package com.example.requestmappingdemo.controller;

import com.example.requestmappingdemo.dao.ArticlesDao;
import com.example.requestmappingdemo.ds.Article;
import com.example.requestmappingdemo.ds.ArticleCriteria;
import com.example.requestmappingdemo.ds.Articles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/articles")
public class ArticleController {

    @Autowired
    private ArticlesDao articlesDao;
    //curl localhost:8080/articles
    @RequestMapping(method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Article>> listArticlesJSON() {
        return ResponseEntity.ok()
                .body(articlesDao.findAll());
    }
    //curl -H 'Accept: application/xml' localhost:8080/articles
    @RequestMapping(method = RequestMethod.GET,produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Articles> listArticlesXML(){
        return ResponseEntity.ok()
                .body(new Articles(articlesDao.findAll()));
    }
    //curl -I localhost:8080/articles/2
    @RequestMapping(method = RequestMethod.GET,path = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Article> getArticleById(@PathVariable int id){
        return articlesDao.findById(id)
                .map(ResponseEntity.ok()::body)
                .orElse(ResponseEntity.notFound().build());
    }
    //curl -I localhost:8080/articles
    @RequestMapping(method = RequestMethod.HEAD,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Article>> getArticlesCount(){
        return ResponseEntity.ok()
                .header("Articles-Count",String.valueOf(articlesDao.count()))
                .body(articlesDao.findAll());
    }
    //curl -X POST localhost:8080/articles/search -H 'Content-Type: application/json' -d '{"bodyLike":"%some%"}'
    @RequestMapping(method = RequestMethod.POST,value = "search",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Article>> searchArticleByCriteria(@RequestBody ArticleCriteria articleCriteria){
        return ResponseEntity.ok().body(articlesDao.findByBodyLikeIgnoreCase(articleCriteria.getBodyLike()));
    }
    //curl -v -X POST localhost:8080/articles -H 'Content-Type: application/json' -d '{"title":"New Article","body":"New Article Content"}'
    @RequestMapping(method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        Article savedArticle=articlesDao.save(article);
        return ResponseEntity.ok().body(savedArticle);
    }
    //curl -v -X PUT localhost:8080/articles -H 'Content-Type: application/json' -d '[{"title":"New Article 1","body":"New Article content"},{"title":"New Article 2","body":New Article Content 2"}]'
    @RequestMapping(method = RequestMethod.PUT,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Article>> updateArticles(@RequestBody List<Article> articles) {
        articlesDao.deleteAll();
        Iterable<Article> savedArticles=articlesDao.saveAll(articles);
        return ResponseEntity.ok().body(savedArticles);
    }
    //curl -v -X PATCH localhost:8080/articles/6 -H 'Content-Type: application/json' -d '{"title":"Updated Article","body":"Updated article content"}'
    @RequestMapping(method = RequestMethod.PATCH,path = "{id}",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Article> updateArticle(@PathVariable int id,@RequestBody Article article) {
        if (articlesDao.existsById(id)){
            article.setId(id);
            Article updateArticle=articlesDao.save(article);
            return ResponseEntity.ok().body(updateArticle);
        }
        else
            return ResponseEntity.notFound().build();
    }
    //curl -v -X DELETE localhost:8080/articles/7
    @RequestMapping(method = RequestMethod.DELETE,path = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteArticleById(@PathVariable int id) {
        if (articlesDao.existsById(id)){
            articlesDao.deleteById(id);
            return ResponseEntity.ok().build();
        }
        else
            return ResponseEntity.notFound().build();
    }

}
