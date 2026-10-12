package com.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.secondhand.common.Result;
import com.secondhand.dto.CommentVO;
import com.secondhand.entity.Comment;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.mapper.CommentMapper;
import com.secondhand.mapper.ProductMapper;
import com.secondhand.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品留言接口
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;

    /** 商品留言列表 */
    @GetMapping("/list/{productId}")
    public Result<List<CommentVO>> list(@PathVariable Long productId) {
        List<Comment> list = commentMapper.selectList(new QueryWrapper<Comment>()
                .eq("product_id", productId)
                .orderByAsc("create_time"));
        List<CommentVO> vos = new ArrayList<>();
        for (Comment c : list) {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(c, vo);
            User u = userMapper.selectById(c.getUserId());
            if (u != null) {
                vo.setNickname(u.getNickname());
                vo.setAvatar(u.getAvatar());
            }
            vos.add(vo);
        }
        return Result.ok(vos);
    }

    /** 发表留言 */
    @PostMapping
    public Result<Comment> add(@RequestBody Comment comment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return Result.error("留言内容不能为空");
        }
        comment.setId(null);
        comment.setUserId(userId);
        comment.setReply(null);
        commentMapper.insert(comment);
        return Result.ok("留言成功", comment);
    }

    /**
     * 我的留言信息：
     * 我发出的留言 + 我发布的商品上收到的留言
     */
    @GetMapping("/mine")
    public Result<List<CommentVO>> mine(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 我发布的商品
        List<Product> myProducts = productMapper.selectList(
                new QueryWrapper<Product>().eq("user_id", userId));
        List<Long> productIds = new ArrayList<>();
        for (Product p : myProducts) {
            productIds.add(p.getId());
        }
        QueryWrapper<Comment> qw = new QueryWrapper<Comment>()
                .and(w -> {
                    w.eq("user_id", userId);
                    if (!productIds.isEmpty()) {
                        w.or().in("product_id", productIds);
                    }
                })
                .orderByDesc("create_time");
        List<Comment> list = commentMapper.selectList(qw);
        List<CommentVO> vos = new ArrayList<>();
        for (Comment c : list) {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(c, vo);
            User u = userMapper.selectById(c.getUserId());
            if (u != null) {
                vo.setNickname(u.getNickname());
                vo.setAvatar(u.getAvatar());
            }
            Product p = productMapper.selectById(c.getProductId());
            if (p != null) {
                vo.setProductId(p.getId());
                vo.setProductTitle(p.getTitle());
            }
            vos.add(vo);
        }
        return Result.ok(vos);
    }

    /** 发布人回复留言 */
    @PostMapping("/{id}/reply")
    public Result<Comment> reply(@PathVariable Long id, @RequestBody Comment replyBody, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Comment db = commentMapper.selectById(id);
        if (db == null) {
            return Result.error("留言不存在");
        }
        Product p = productMapper.selectById(db.getProductId());
        if (p == null || !p.getUserId().equals(userId)) {
            return Result.error("只有商品发布人才能回复留言");
        }
        if (replyBody.getReply() == null || replyBody.getReply().trim().isEmpty()) {
            return Result.error("回复内容不能为空");
        }
        db.setReply(replyBody.getReply());
        commentMapper.updateById(db);
        return Result.ok("回复成功", db);
    }
}
