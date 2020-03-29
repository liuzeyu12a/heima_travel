package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.FavoriteDao;
import cn.itcast.travel.dao.impl.FavoriteDaoImpl;
import cn.itcast.travel.domain.Favorite;
import cn.itcast.travel.service.FavoriteService;

public class FavoriteServiceImple implements FavoriteService {

    private FavoriteDao dao = new FavoriteDaoImpl();
    @Override
    public boolean isFavorite(String rid, int uid) {
        Favorite favorite = dao.findByUidAndRid(Integer.parseInt(rid), uid);
        if(favorite != null){
            return true;
        }
        return false;
    }

    @Override
    public int findByRid(int rid) {
        return dao.findCountByRid(rid);
    }

    @Override
    public void addFavorite(String rid, int uid) {
        dao.add(Integer.parseInt(rid),uid);
    }
}
