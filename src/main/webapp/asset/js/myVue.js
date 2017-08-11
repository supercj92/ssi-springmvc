var app1 = new Vue({
    el:'#app1',
    data:{
        message:'hello vue'
    }
});
var app2 = new Vue({
    el:'#app2',
    data:{
        message:'span的title属性'
    }
});
var app3 = new Vue({
    el:'#app3',
    data:{
        seen:false
    }
});
var app4 = new Vue({
    el:'#app4',
    data:{
        items : [
            {text:'text1'},
            {text:'text2'},
            {text:'text3'}
        ]
    }
});
var app5 = new Vue({
    el:'#app5',
    data:{
        data:'abcdef'
    },
    methods:{
        reverse:function () {
            this.data = this.data.split('').reverse().join('');
        }
    }
});
var app6 = new Vue({
    el:'#app6',
    data:{
        data:'初始数据'
    }
});
//定义组件
Vue.component('todo-item', {
    props: ['todo'],
    template: '<li>{{ todo.text }}</li>'
})
var app7 = new Vue({
    el: '#app7',
    data: {
        groceryList: [
            { id: 0, text: '蔬菜' },
            { id: 1, text: '奶酪' },
            { id: 2, text: '随便其他什么人吃的东西' }
        ]
    }
})
