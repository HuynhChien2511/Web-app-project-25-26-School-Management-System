package com.school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.school.model.ForumPost;
import com.school.util.DatabaseConnection;

public class ForumDAO {

    // Get all top-level posts for a course
    public List<ForumPost> getTopLevelPostsByCourse(int courseId) {
        List<ForumPost> posts = new ArrayList<>();
        String sql = "SELECT fp.*, u.full_name as author_name, u.user_type as author_type, " +
                     "c.course_code, c.course_name, " +
                     "(SELECT COUNT(*) FROM forum_posts WHERE parent_post_id = fp.post_id) as reply_count " +
                     "FROM forum_posts fp " +
                     "JOIN users u ON fp.author_id = u.user_id " +
                     "JOIN courses c ON fp.course_id = c.course_id " +
                     "WHERE fp.course_id = ? AND fp.parent_post_id IS NULL " +
                     "ORDER BY fp.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                posts.add(extractForumPostFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // Get all replies for a specific post
    public List<ForumPost> getRepliesByPostId(int postId) {
        List<ForumPost> replies = new ArrayList<>();
        String sql = "SELECT fp.*, u.full_name as author_name, u.user_type as author_type, " +
                     "c.course_code, c.course_name " +
                     "FROM forum_posts fp " +
                     "JOIN users u ON fp.author_id = u.user_id " +
                     "JOIN courses c ON fp.course_id = c.course_id " +
                     "WHERE fp.parent_post_id = ? " +
                     "ORDER BY fp.created_at ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                replies.add(extractForumPostFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }

    // Get a specific forum post by ID
    public ForumPost getPostById(int postId) {
        String sql = "SELECT fp.*, u.full_name as author_name, u.user_type as author_type, " +
                     "c.course_code, c.course_name, " +
                     "(SELECT COUNT(*) FROM forum_posts WHERE parent_post_id = fp.post_id) as reply_count " +
                     "FROM forum_posts fp " +
                     "JOIN users u ON fp.author_id = u.user_id " +
                     "JOIN courses c ON fp.course_id = c.course_id " +
                     "WHERE fp.post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractForumPostFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Create a new top-level forum post
    public boolean createTopLevelPost(ForumPost post) {
        String sql = "INSERT INTO forum_posts (course_id, author_id, title, content) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, post.getCourseId());
            stmt.setInt(2, post.getAuthorId());
            stmt.setString(3, post.getTitle());
            stmt.setString(4, post.getContent());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Create a reply to an existing post
    public boolean createReply(ForumPost reply) {
        String sql = "INSERT INTO forum_posts (course_id, author_id, parent_post_id, content) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reply.getCourseId());
            stmt.setInt(2, reply.getAuthorId());
            stmt.setInt(3, reply.getParentPostId());
            stmt.setString(4, reply.getContent());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update a forum post
    public boolean updatePost(ForumPost post) {
        String sql;
        if (post.isTopLevel()) {
            sql = "UPDATE forum_posts SET title = ?, content = ? WHERE post_id = ?";
        } else {
            sql = "UPDATE forum_posts SET content = ? WHERE post_id = ?";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (post.isTopLevel()) {
                stmt.setString(1, post.getTitle());
                stmt.setString(2, post.getContent());
                stmt.setInt(3, post.getPostId());
            } else {
                stmt.setString(1, post.getContent());
                stmt.setInt(2, post.getPostId());
            }
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a forum post (and all its replies due to CASCADE)
    public boolean deletePost(int postId) {
        String sql = "DELETE FROM forum_posts WHERE post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get recent posts by a user across all courses
    public List<ForumPost> getPostsByUser(int userId, int limit) {
        List<ForumPost> posts = new ArrayList<>();
        String sql = "SELECT fp.*, u.full_name as author_name, u.user_type as author_type, " +
                     "c.course_code, c.course_name, " +
                     "(SELECT COUNT(*) FROM forum_posts WHERE parent_post_id = fp.post_id) as reply_count " +
                     "FROM forum_posts fp " +
                     "JOIN users u ON fp.author_id = u.user_id " +
                     "JOIN courses c ON fp.course_id = c.course_id " +
                     "WHERE fp.author_id = ? " +
                     "ORDER BY fp.created_at DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                posts.add(extractForumPostFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // Helper method to extract forum post from ResultSet
    private ForumPost extractForumPostFromResultSet(ResultSet rs) throws SQLException {
        ForumPost post = new ForumPost();
        post.setPostId(rs.getInt("post_id"));
        post.setCourseId(rs.getInt("course_id"));
        post.setCourseCode(rs.getString("course_code"));
        post.setCourseName(rs.getString("course_name"));
        post.setAuthorId(rs.getInt("author_id"));
        post.setAuthorName(rs.getString("author_name"));
        post.setAuthorType(rs.getString("author_type"));
        
        // Handle nullable parent_post_id
        int parentPostId = rs.getInt("parent_post_id");
        if (!rs.wasNull()) {
            post.setParentPostId(parentPostId);
        }
        
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        post.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Reply count is only available in some queries
        try {
            post.setReplyCount(rs.getInt("reply_count"));
        } catch (SQLException e) {
            // Column doesn't exist in this query, that's okay
            post.setReplyCount(0);
        }
        
        return post;
    }
}
