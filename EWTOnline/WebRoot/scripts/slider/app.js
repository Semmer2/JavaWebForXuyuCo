Ext.require('Ext.slider.*');
var myslider=null;
Ext.onReady(function(){ 
    myslider=Ext.create('Ext.slider.Single', {
        renderTo: 'slider1',
        hideLabel: false,
        useTips: true,
        height: 100,
        vertical: true,
        minValue: 100,
        maxValue: 200,
		value:100		
    });	
});




