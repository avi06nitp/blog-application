package com.BlogApp.services.Impl;

import com.BlogApp.entities.Category;
import com.BlogApp.entities.Post;
import com.BlogApp.entities.User;
import com.BlogApp.exception.ResourceNotFoundException;
import com.BlogApp.payloads.PostDto;
import com.BlogApp.payloads.PostResponse;
import com.BlogApp.repositories.CategoryRepo;
import com.BlogApp.repositories.PostRepo;
import com.BlogApp.repositories.UserRepo;
import com.BlogApp.services.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;


    @Override
    public PostDto createPost(PostDto postDto, Integer userId, Integer categoryId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));
        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

        Post post = this.modelMapper.map(postDto, Post.class);
        post.setImageName("default.png");
        post.setAddedDate(new Date());
        post.setUser(user);
        post.setCategory(category);

        Post newPost = this.postRepo.save(post);
        return this.modelMapper.map(newPost, PostDto.class);
    }


    @Override
    public PostDto updatePost(PostDto postDto, Integer postId) {
        Post post = this.postRepo.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setImageName(postDto.getImageName());
        Post updatedPost = this.postRepo.save(post);

        return this.modelMapper.map(updatedPost, PostDto.class);
    }

    @Override
    public void deletePost(Integer postId) {
        Post post = this.postRepo.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
        this.postRepo.delete(post);

    }

    @Override
    public PostResponse getAllPosts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Pageable pageable;
        if (sortDir.equalsIgnoreCase("dsc")) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
        } else {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        }

        Page<Post> pagePost = this.postRepo.findAll(pageable);
        List<Post> allPosts = pagePost.getContent();
        List<PostDto> postDtos = allPosts.stream()
                .map(post -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
        PostResponse postResponse = new PostResponse();
        postResponse.setContent(postDtos);
        postResponse.setPageNumber(pagePost.getNumber());
        postResponse.setPageSize(pagePost.getSize());
        postResponse.setTotalPages(pagePost.getTotalPages());
        postResponse.setTotalElements(pagePost.getTotalElements());
        postResponse.setLastPage(pagePost.isLast());

        return postResponse;
    }


    @Override
    public PostDto getPostById(Integer postId) {
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
        PostDto postDto = this.modelMapper.map(post, PostDto.class);
        return postDto;
    }

    @Override
    public List<PostDto> searchPosts(String search) {
        List<Post> posts = postRepo.findByTitleContainingIgnoreCase(search);
        posts.addAll(postRepo.findByContentContainingIgnoreCase(search));
        List<PostDto> postDtos=posts.stream().map((post)->this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
        return  postDtos;
    }

    @Override
    public List<PostDto> getPostByCategory(Integer categoryId) {
        Category category = this.categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
        List<Post> posts = this.postRepo.findByCategory(category);

        List<PostDto> postDtoStream = posts.stream()
                .map((post) -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        return postDtoStream;
    }

    @Override
    public List<PostDto> getPostByUser(Integer userId) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));
        List<Post> posts = this.postRepo.findByUser(user);
        List<PostDto> postDtos = posts.stream()
                .map(post -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }
}
