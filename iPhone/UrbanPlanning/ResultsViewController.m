////
////  ResultsViewController.m
////  Urban Planning
////
////  Created by George Liaros on 3/6/14.
////  Copyright (c) 2014 George Liaros. All rights reserved.
////
//
//#import "ResultsViewController.h"
//
//@interface ResultsViewController ()
//@property (nonatomic, strong) CPTGraphHostingView *hostView;
//@property (nonatomic, strong) CPTTheme *selectedTheme;
//
//-(void)initPlot;
//-(void)configureHost;
//-(void)configureGraph;
//-(void)configureChart;
//-(void)configureLegend;
//@end
//
//@implementation ResultsViewController
//@synthesize hostView;// = hostView_;
//@synthesize selectedTheme;// = selectedTheme_;
//
//
//- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
//{
//    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
//    if (self) {
//        // Custom initialization
//    }
//    return self;
//}
//
//-(void)viewDidAppear:(BOOL)animated {
//    [super viewDidAppear:animated];
//    // The plot is initialized here, since the view bounds have not transformed for landscape till now
//    [self initPlot];
//}
//- (void)viewDidLoad
//{
//    [super viewDidLoad];
//    // Do any additional setup after loading the view from its nib.
//}
//
//- (void)didReceiveMemoryWarning
//{
//    [super didReceiveMemoryWarning];
//    // Dispose of any resources that can be recreated.
//}
//
//-(NSUInteger)numberOfRecordsForPlot:(CPTPlot *)plot {
////        NSLog(@"dataLabelForPlot");
//	return 3;
//}
//
//-(NSNumber *)numberForPlot:(CPTPlot *)plot field:(NSUInteger)fieldEnum recordIndex:(NSUInteger)index {
//	if (CPTPieChartFieldSliceWidth == fieldEnum) {
//		return [NSNumber numberWithDouble:index * 3.5];
//	}
//	return [NSNumber numberWithDouble:index * 8.5];
////        NSLog(@"dataLabelForPlot");
//}
//
//-(CPTLayer *)dataLabelForPlot:(CPTPlot *)plot recordIndex:(NSUInteger)index {
//	// 1 - Define label text style
//	static CPTMutableTextStyle *labelText = nil;
//	if (!labelText) {
//		labelText= [[CPTMutableTextStyle alloc] init];
//		labelText.color = [CPTColor grayColor];
//	}
//	// 2 - Calculate portfolio total value
//	NSDecimalNumber *portfolioSum = [NSDecimalNumber zero];
////	for (NSDecimalNumber *price in [[CPDStockPriceStore sharedInstance] dailyPortfolioPrices]) {
////		portfolioSum = [portfolioSum decimalNumberByAdding:price];
////	}
//	// 3 - Calculate percentage value
//	NSNumber *price = [NSNumber numberWithDouble:6.5 *index];
////	NSDecimalNumber *percent = [price decimalNumberByDividingBy:portfolioSum];
//	// 4 - Set up display label
//	NSString *labelValue = [NSString stringWithFormat:@"$%0.2f USD (%0.1f %%)", [price floatValue], ([price floatValue] * 100.0f)];
//	// 5 - Create and return layer with label text
////    NSLog(@"dataLabelForPlot");
//	return [[CPTTextLayer alloc] initWithText:labelValue style:labelText];
//}
//
//-(NSString *)legendTitleForPieChart:(CPTPieChart *)pieChart recordIndex:(NSUInteger)index {
////	if (index < [[[CPDStockPriceStore sharedInstance] tickerSymbols] count]) {
////		return [[[CPDStockPriceStore sharedInstance] tickerSymbols] objectAtIndex:index];
////	}
////        NSLog(@"dataLabelForPlot");
//	return [NSString stringWithFormat:@"LALAKIS %d", index];
//}
//
//-(void)initPlot {
//    [self configureHost];
//    [self configureGraph];
//    [self configureChart];
//    [self configureLegend];
//}
//
//-(void)configureHost {
//	// 1 - Set up view frame
//	CGRect parentRect = self.view.bounds;
////	CGSize toolbarSize = self.toolbar.bounds.size;
////    CGSize toolbarSize = self.navigationController.toolbar.bounds.size;
////	parentRect = CGRectMake(parentRect.origin.x,
////							(parentRect.origin.y + toolbarSize.height),
////							parentRect.size.width,
////							(parentRect.size.height - toolbarSize.height));
//	// 2 - Create host view
//	self.hostView = [(CPTGraphHostingView *) [CPTGraphHostingView alloc] initWithFrame:parentRect];
////	self.hostView.allowPinchScaling = NO;
//	[self.view addSubview:self.hostView];
//}
//
//-(void)configureGraph {
//	// 1 - Create and initialise graph
//	CPTGraph *graph = [[CPTXYGraph alloc] initWithFrame:self.hostView.bounds];
//	self.hostView.hostedGraph = graph;
//	graph.paddingLeft = 0.0f;
//	graph.paddingTop = 0.0f;
//	graph.paddingRight = 0.0f;
//	graph.paddingBottom = 0.0f;
//	graph.axisSet = nil;
//	// 2 - Set up text style
//	CPTMutableTextStyle *textStyle = [CPTMutableTextStyle textStyle];
//	textStyle.color = [CPTColor grayColor];
//	textStyle.fontName = @"Helvetica-Bold";
//	textStyle.fontSize = 16.0f;
//	// 3 - Configure title
//	NSString *title = @"Portfolio Prices: May 1, 2012";
//	graph.title = title;
//	graph.titleTextStyle = textStyle;
//	graph.titlePlotAreaFrameAnchor = CPTRectAnchorTop;
//	graph.titleDisplacement = CGPointMake(0.0f, -12.0f);
//	// 4 - Set theme
//	self.selectedTheme = [CPTTheme themeNamed:kCPTPlainWhiteTheme];
//	[graph applyTheme:self.selectedTheme];
//}
//
//-(void)configureChart {
//	// 1 - Get reference to graph
//	CPTGraph *graph = self.hostView.hostedGraph;
//	// 2 - Create chart
//	CPTPieChart *pieChart = [[CPTPieChart alloc] init];
//	pieChart.dataSource = self;
//	pieChart.delegate = self;
//	pieChart.pieRadius = (self.hostView.bounds.size.height * 0.7) / 2;
//	pieChart.identifier = graph.title;
//	pieChart.startAngle = M_PI_4;
//	pieChart.sliceDirection = CPTPieDirectionClockwise;
//	// 3 - Create gradient
//	CPTGradient *overlayGradient = [[CPTGradient alloc] init];
//	overlayGradient.gradientType = CPTGradientTypeRadial;
//	overlayGradient = [overlayGradient addColorStop:[[CPTColor blackColor] colorWithAlphaComponent:0.0] atPosition:0.9];
//	overlayGradient = [overlayGradient addColorStop:[[CPTColor blackColor] colorWithAlphaComponent:0.4] atPosition:1.0];
//	pieChart.overlayFill = [CPTFill fillWithGradient:overlayGradient];
//	// 4 - Add chart to graph
//	[graph addPlot:pieChart];
//}
//
//-(void)configureLegend {
//	// 1 - Get graph instance
//	CPTGraph *graph = self.hostView.hostedGraph;
//	// 2 - Create legend
//	CPTLegend *theLegend = [CPTLegend legendWithGraph:graph];
//	// 3 - Configure legen
//	theLegend.numberOfColumns = 1;
//	theLegend.fill = [CPTFill fillWithColor:[CPTColor whiteColor]];
//	theLegend.borderLineStyle = [CPTLineStyle lineStyle];
//	theLegend.cornerRadius = 5.0;
//	// 4 - Add legend to graph
//	graph.legend = theLegend;
//	graph.legendAnchor = CPTRectAnchorRight;
//	CGFloat legendPadding = -(self.view.bounds.size.width / 8);
//	graph.legendDisplacement = CGPointMake(legendPadding, 0.0);
//}
//
//
//@end
