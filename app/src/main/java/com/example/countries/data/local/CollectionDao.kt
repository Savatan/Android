package com.example.countries.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class CollectionWithCount(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val itemCount: Int
)

@Dao
interface CollectionDao {

    @Query(
        "SELECT c.id AS id, c.name AS name, c.createdAt AS createdAt, " +
            "COUNT(i.countryCode) AS itemCount FROM collections c " +
            "LEFT JOIN collection_items i ON i.collectionId = c.id " +
            "GROUP BY c.id ORDER BY c.createdAt DESC"
    )
    fun observeCollectionsWithCount(): Flow<List<CollectionWithCount>>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    fun observeCollection(id: Long): Flow<CollectionEntity?>

    @Query(
        "SELECT cc.* FROM collection_items i " +
            "INNER JOIN cached_countries cc ON cc.code = i.countryCode " +
            "WHERE i.collectionId = :collectionId ORDER BY i.addedAt DESC"
    )
    fun observeItems(collectionId: Long): Flow<List<CachedCountryEntity>>

    @Query(
        "SELECT c.* FROM collections c " +
            "INNER JOIN collection_items i ON i.collectionId = c.id " +
            "WHERE i.countryCode = :code ORDER BY c.name ASC"
    )
    fun observeCollectionsForCountry(code: String): Flow<List<CollectionEntity>>

    @Insert
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteCollection(id: Long)

    @Query("DELETE FROM collection_items WHERE collectionId = :id")
    suspend fun deleteItemsForCollection(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CollectionItemEntity)

    @Query("DELETE FROM collection_items WHERE collectionId = :collectionId AND countryCode = :code")
    suspend fun deleteItem(collectionId: Long, code: String)

    @Query("SELECT COUNT(*) FROM collection_items WHERE collectionId = :collectionId AND countryCode = :code")
    suspend fun countItem(collectionId: Long, code: String): Int
}
