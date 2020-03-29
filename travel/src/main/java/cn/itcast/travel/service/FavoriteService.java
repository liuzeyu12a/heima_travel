package cn.itcast.travel.service;

public interface FavoriteService {
    public boolean isFavorite(String rid, int uid);

    public int findByRid(int rid);

    void addFavorite(String rid, int uid);
}
