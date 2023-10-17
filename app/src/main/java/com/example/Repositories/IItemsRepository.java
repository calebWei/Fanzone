package com.example.Repositories;

import android.content.Context;

import com.example.Entities.IItem;

import java.util.ArrayList;
import java.util.List;

public interface IItemsRepository {

    List<IItem> fetchAllItems(Context context);

}
