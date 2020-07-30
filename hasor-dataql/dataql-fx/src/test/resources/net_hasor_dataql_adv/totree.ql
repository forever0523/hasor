// 层次化：把带有 parent 属性的数据转换成 tree 结构
//  - 需要用到集合包的 filter 函数
//  - 实现思路是：递归

import 'net.hasor.dataql.fx.basic.CollectionUdfSource' as collect;

var dataSet = [
    {'id': 1, 'parent_id':null, 'label' : 't1'},
    {'id': 2, 'parent_id':1   , 'label' : 't2'},
    {'id': 3, 'parent_id':1   , 'label' : 't3'},
    {'id': 4, 'parent_id':2   , 'label' : 't4'},
    {'id': 5, 'parent_id':null, 'label' : 't5'}
]

var nodeFmt = (dat) -> {
    return dat => {
        "id",
        "label",
        "children" : collect.filter(dataSet, (test)-> { return (test.parent_id == dat.id); }) => [ nodeFmt(#) ]
    }
}

return collect.filter(dataSet, (test)-> { return (test.parent_id == null); }) => [ nodeFmt(#) ]
