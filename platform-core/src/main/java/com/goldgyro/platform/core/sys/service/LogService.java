package com.goldgyro.platform.core.sys.service;

/**
 * @author wg2993
 *
 */
public interface LogService {
	void add(int logType, long userId, long targetId, String ip);
}
