package com.goldgyro.platform.core.sys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goldgyro.platform.core.sys.dao.UserDao;
import com.goldgyro.platform.core.sys.service.UserEventService;

/**
 * @author wg2993
 * @version 2018/07/08.
 */
@Service
public class UserEventServiceImpl implements UserEventService {
    @Autowired
    private UserDao userDao;

	@Override
	public void identityPost(Long userId, long postId, boolean identity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identityComment(Long userId, long commentId, boolean identity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identityFollow(Long userId, long followId, boolean identity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identityFans(Long userId, long fansId, boolean identity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identityFavors(Long userId, boolean identity, int targetType, long targetId) {
		// TODO Auto-generated method stub
		
	}

    /*@Override
    @Transactional
    public void identityPost(Long userId, long postId, boolean identity) {
        UserPO po = userDao.findOne(userId);

        if (po != null) {
            po.setPosts(po.getPosts() + ((identity) ? 1 : -1));
        }
    }

    @Override
    @Transactional
    public void identityComment(Long userId, long commentId, boolean identity) {
        UserPO po = userDao.findOne(userId);

        if (po != null) {
            po.setComments(po.getComments() + ((identity) ? 1 : -1));
        }
    }

    @Override
    @Transactional
    public void identityFollow(Long userId, long followId, boolean identity) {
        UserPO po = userDao.findOne(userId);

        if (po != null) {
            po.setFollows(po.getFollows() + ((identity) ? 1 : -1));
        }
    }

    @Override
    @Transactional
    public void identityFans(Long userId, long fansId, boolean identity) {
        UserPO po = userDao.findOne(userId);

        if (po != null) {
            po.setFans(po.getFans() + ((identity) ? 1 : -1));
        }
    }

    @Override
    @Transactional
    public void identityFavors(Long userId, boolean identity, int targetType, long targetId) {
        UserPO po = userDao.findOne(userId);

        if (po != null) {
            po.setFavors(po.getFavors() + ((identity) ? 1 : -1));
        }
    }*/
}
