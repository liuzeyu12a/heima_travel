package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Favorite;

public interface FavoriteDao {
    public Favorite findByUidAndRid(int rid,int uid);

    public int findCountByRid(int rid);

    void add(int i, int uid);
}
