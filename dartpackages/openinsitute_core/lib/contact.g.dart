// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'contact.dart';

// **************************************************************************
// IsarCollectionGenerator
// **************************************************************************

// ignore_for_file: duplicate_ignore, non_constant_identifier_names, invalid_use_of_protected_member, unnecessary_cast

extension GetContactCollection on Isar {
  IsarCollection<Contact> get contacts {
    return getCollection('Contact');
  }
}

final ContactSchema = CollectionSchema(
  name: 'Contact',
  schema:
      '{"name":"Contact","idName":"id","properties":[{"name":"name","type":"String"}],"indexes":[],"links":[]}',
  nativeAdapter: const _ContactNativeAdapter(),
  webAdapter: const _ContactWebAdapter(),
  idName: 'id',
  propertyIds: {'name': 0},
  listProperties: {},
  indexIds: {},
  indexTypes: {},
  linkIds: {},
  backlinkIds: {},
  linkedCollections: [],
  getId: (obj) {
    if (obj.id == Isar.autoIncrement) {
      return null;
    } else {
      return obj.id;
    }
  },
  setId: (obj, id) => obj.id = id,
  getLinks: (obj) => [],
  version: 2,
);

class _ContactWebAdapter extends IsarWebTypeAdapter<Contact> {
  const _ContactWebAdapter();

  @override
  Object serialize(IsarCollection<Contact> collection, Contact object) {
    final jsObj = IsarNative.newJsObject();
    IsarNative.jsObjectSet(jsObj, 'id', object.id);
    IsarNative.jsObjectSet(jsObj, 'name', object.name);
    return jsObj;
  }

  @override
  Contact deserialize(IsarCollection<Contact> collection, dynamic jsObj) {
    final object = Contact();
    object.id = IsarNative.jsObjectGet(jsObj, 'id');
    object.name = IsarNative.jsObjectGet(jsObj, 'name') ?? '';
    return object;
  }

  @override
  P deserializeProperty<P>(Object jsObj, String propertyName) {
    switch (propertyName) {
      case 'id':
        return (IsarNative.jsObjectGet(jsObj, 'id')) as P;
      case 'name':
        return (IsarNative.jsObjectGet(jsObj, 'name') ?? '') as P;
      default:
        throw 'Illegal propertyName';
    }
  }

  @override
  void attachLinks(Isar isar, int id, Contact object) {}
}

class _ContactNativeAdapter extends IsarNativeTypeAdapter<Contact> {
  const _ContactNativeAdapter();

  @override
  void serialize(IsarCollection<Contact> collection, IsarRawObject rawObj,
      Contact object, int staticSize, List<int> offsets, AdapterAlloc alloc) {
    var dynamicSize = 0;
    final value0 = object.name;
    final _name = IsarBinaryWriter.utf8Encoder.convert(value0);
    dynamicSize += (_name.length) as int;
    final size = staticSize + dynamicSize;

    rawObj.buffer = alloc(size);
    rawObj.buffer_length = size;
    final buffer = IsarNative.bufAsBytes(rawObj.buffer, size);
    final writer = IsarBinaryWriter(buffer, staticSize);
    writer.writeBytes(offsets[0], _name);
  }

  @override
  Contact deserialize(IsarCollection<Contact> collection, int id,
      IsarBinaryReader reader, List<int> offsets) {
    final object = Contact();
    object.id = id;
    object.name = reader.readString(offsets[0]);
    return object;
  }

  @override
  P deserializeProperty<P>(
      int id, IsarBinaryReader reader, int propertyIndex, int offset) {
    switch (propertyIndex) {
      case -1:
        return id as P;
      case 0:
        return (reader.readString(offset)) as P;
      default:
        throw 'Illegal propertyIndex';
    }
  }

  @override
  void attachLinks(Isar isar, int id, Contact object) {}
}

extension ContactQueryWhereSort on QueryBuilder<Contact, Contact, QWhere> {
  QueryBuilder<Contact, Contact, QAfterWhere> anyId() {
    return addWhereClauseInternal(const WhereClause(indexName: null));
  }
}

extension ContactQueryWhere on QueryBuilder<Contact, Contact, QWhereClause> {
  QueryBuilder<Contact, Contact, QAfterWhereClause> idEqualTo(int? id) {
    return addWhereClauseInternal(WhereClause(
      indexName: null,
      lower: [id],
      includeLower: true,
      upper: [id],
      includeUpper: true,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterWhereClause> idNotEqualTo(int? id) {
    if (whereSortInternal == Sort.asc) {
      return addWhereClauseInternal(WhereClause(
        indexName: null,
        upper: [id],
        includeUpper: false,
      )).addWhereClauseInternal(WhereClause(
        indexName: null,
        lower: [id],
        includeLower: false,
      ));
    } else {
      return addWhereClauseInternal(WhereClause(
        indexName: null,
        lower: [id],
        includeLower: false,
      )).addWhereClauseInternal(WhereClause(
        indexName: null,
        upper: [id],
        includeUpper: false,
      ));
    }
  }

  QueryBuilder<Contact, Contact, QAfterWhereClause> idGreaterThan(
    int? id, {
    bool include = false,
  }) {
    return addWhereClauseInternal(WhereClause(
      indexName: null,
      lower: [id],
      includeLower: include,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterWhereClause> idLessThan(
    int? id, {
    bool include = false,
  }) {
    return addWhereClauseInternal(WhereClause(
      indexName: null,
      upper: [id],
      includeUpper: include,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterWhereClause> idBetween(
    int? lowerId,
    int? upperId, {
    bool includeLower = true,
    bool includeUpper = true,
  }) {
    return addWhereClauseInternal(WhereClause(
      indexName: null,
      lower: [lowerId],
      includeLower: includeLower,
      upper: [upperId],
      includeUpper: includeUpper,
    ));
  }
}

extension ContactQueryFilter
    on QueryBuilder<Contact, Contact, QFilterCondition> {
  QueryBuilder<Contact, Contact, QAfterFilterCondition> idIsNull() {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.isNull,
      property: 'id',
      value: null,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> idEqualTo(int? value) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.eq,
      property: 'id',
      value: value,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> idGreaterThan(
    int? value, {
    bool include = false,
  }) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.gt,
      include: include,
      property: 'id',
      value: value,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> idLessThan(
    int? value, {
    bool include = false,
  }) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.lt,
      include: include,
      property: 'id',
      value: value,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> idBetween(
    int? lower,
    int? upper, {
    bool includeLower = true,
    bool includeUpper = true,
  }) {
    return addFilterConditionInternal(FilterCondition.between(
      property: 'id',
      lower: lower,
      includeLower: includeLower,
      upper: upper,
      includeUpper: includeUpper,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> nameEqualTo(
    String value, {
    bool caseSensitive = true,
  }) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.eq,
      property: 'name',
      value: value,
      caseSensitive: caseSensitive,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> nameGreaterThan(
    String value, {
    bool caseSensitive = true,
    bool include = false,
  }) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.gt,
      include: include,
      property: 'name',
      value: value,
      caseSensitive: caseSensitive,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> nameLessThan(
    String value, {
    bool caseSensitive = true,
    bool include = false,
  }) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.lt,
      include: include,
      property: 'name',
      value: value,
      caseSensitive: caseSensitive,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> nameBetween(
    String lower,
    String upper, {
    bool caseSensitive = true,
    bool includeLower = true,
    bool includeUpper = true,
  }) {
    return addFilterConditionInternal(FilterCondition.between(
      property: 'name',
      lower: lower,
      includeLower: includeLower,
      upper: upper,
      includeUpper: includeUpper,
      caseSensitive: caseSensitive,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> nameStartsWith(
    String value, {
    bool caseSensitive = true,
  }) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.startsWith,
      property: 'name',
      value: value,
      caseSensitive: caseSensitive,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> nameEndsWith(
    String value, {
    bool caseSensitive = true,
  }) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.endsWith,
      property: 'name',
      value: value,
      caseSensitive: caseSensitive,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> nameContains(
      String value,
      {bool caseSensitive = true}) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.contains,
      property: 'name',
      value: value,
      caseSensitive: caseSensitive,
    ));
  }

  QueryBuilder<Contact, Contact, QAfterFilterCondition> nameMatches(
      String pattern,
      {bool caseSensitive = true}) {
    return addFilterConditionInternal(FilterCondition(
      type: ConditionType.matches,
      property: 'name',
      value: pattern,
      caseSensitive: caseSensitive,
    ));
  }
}

extension ContactQueryLinks
    on QueryBuilder<Contact, Contact, QFilterCondition> {}

extension ContactQueryWhereSortBy on QueryBuilder<Contact, Contact, QSortBy> {
  QueryBuilder<Contact, Contact, QAfterSortBy> sortById() {
    return addSortByInternal('id', Sort.asc);
  }

  QueryBuilder<Contact, Contact, QAfterSortBy> sortByIdDesc() {
    return addSortByInternal('id', Sort.desc);
  }

  QueryBuilder<Contact, Contact, QAfterSortBy> sortByName() {
    return addSortByInternal('name', Sort.asc);
  }

  QueryBuilder<Contact, Contact, QAfterSortBy> sortByNameDesc() {
    return addSortByInternal('name', Sort.desc);
  }
}

extension ContactQueryWhereSortThenBy
    on QueryBuilder<Contact, Contact, QSortThenBy> {
  QueryBuilder<Contact, Contact, QAfterSortBy> thenById() {
    return addSortByInternal('id', Sort.asc);
  }

  QueryBuilder<Contact, Contact, QAfterSortBy> thenByIdDesc() {
    return addSortByInternal('id', Sort.desc);
  }

  QueryBuilder<Contact, Contact, QAfterSortBy> thenByName() {
    return addSortByInternal('name', Sort.asc);
  }

  QueryBuilder<Contact, Contact, QAfterSortBy> thenByNameDesc() {
    return addSortByInternal('name', Sort.desc);
  }
}

extension ContactQueryWhereDistinct
    on QueryBuilder<Contact, Contact, QDistinct> {
  QueryBuilder<Contact, Contact, QDistinct> distinctById() {
    return addDistinctByInternal('id');
  }

  QueryBuilder<Contact, Contact, QDistinct> distinctByName(
      {bool caseSensitive = true}) {
    return addDistinctByInternal('name', caseSensitive: caseSensitive);
  }
}

extension ContactQueryProperty
    on QueryBuilder<Contact, Contact, QQueryProperty> {
  QueryBuilder<Contact, int?, QQueryOperations> idProperty() {
    return addPropertyNameInternal('id');
  }

  QueryBuilder<Contact, String, QQueryOperations> nameProperty() {
    return addPropertyNameInternal('name');
  }
}
