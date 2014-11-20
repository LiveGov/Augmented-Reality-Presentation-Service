//
//  DataHandler.m
//  LiveAndGov Urban Planning
//
//  Created by George Liaros on 2/21/14.
//
//

#import "DataHandler.h"

@implementation DataHandler
//https://testservicecenter.yucat.com/servicecenter/api/user/anonymous
//https://testservicecenter.yucat.com/servicecenter/api/account/permission
//3d http://augreal.mklab.iti.gr/Models3D_DB/41/1/AR_41_1junaio.zip
//http://augreal.mklab.iti.gr/api_Metaio_v2/IBS/index.php

- (id) init
{
    self = [super init];
//    if(self)
//        basque = @{@"18 - 35" : @"18 - 35"
//                   , @"36 - 65" : @"36 - 65"
//                   , @"About" : @"Zeri buruz"
//                   , @"Age" : @"Adina"
//                   , @"AR Layer" : @"EA Kapa"
//                   , @"Area of residence" : @"Auzoa"
//                   , @"Basque" : @"Euskara"
//                   , @"Billboard" : @"Etiketak"
//                   , @"Cannot connect to server.\nPlease check your internet connection" : @"Ez da possible zerbitzariarekin konektatzea"
//                   , @"Done" : @"Eginda"
//                   , @"Downloading ..." : @"Deskargatzen..."
//                   , @"Empty Fields" : @"Hutsuneak betetzeko"
//                   , @"English" : @"Ingelesa"
//                   , @"Error" : @"Akatza"
//                   , @"Error loading questionnaire" : @"Akatsa galdeketa kargatzerakoan"
//                   , @"Female" : @"Emakumea"
//                   , @"Gender" : @"Genero"
//                   , @"Irazagorria" : @"Irazagorria"
//                   , @"Language" : @"Hizkuntza"
//                   , @"List" : @"Lista"
//                   , @"Male" : @"Gizona"
//                   , @"Map" : @"Mapa"
//                   , @"Navigation" : @"Nabigazioa"
////                   , @"Nearby" : @"Ingurukoa"
//                   , @"Nearby" : @"Hurbiltasunaren arabera"
//                   , @"No" : @"Ez"
//                   , @"None" : @"Bat ere"
//                   , @"No internet connection" : @"Internetekin konexio falta dago"
//                   , @"OK" : @"Zuzena"
//                   , @"Over the 65" : @"65 urtetik gorakoak"
//                   , @"Personal Information" : @"Datu pertsonalak"
//                   , @"Please fill all the questions" : @"Mesedez erantzun itzazu galdera guztiak"
//                   , @"Please provide your information" : @"Mesedez bete itzazu zure datuak."
//                   , @"Ponton Urarte" : @"Ponton Urarte"
//                   , @"Provide Opinion" : @"Iritzia eman"
//                   , @"Provide opinion" : @"Iritzia eman"
//                   , @"Questionnaire is not valid" : @"Kuestionarioa ez da zuzena"
//                   , @"Questionnaire updated." : @"Kuestionarioa eguneratuta"
//                   , @"Refresh" : @"Freskatu"
//                   , @"Resident in Gordexola" : @"Gordexolan auzokoa"
//                   , @"Results" : @"Emaitzak"
//                   , @"Save" : @"Gorde"
////                   , @"Scan" : @"Irudi bidez"
//                   , @"Scan" : @"Irudiaren arabera"
//                   , @"Select an answer" : @"Erantzun bat aukeratu"
//                   , @"Something went wrong while submitting the questionnaire" : @"Kuestionario hau bidaltzerakoan akatza agertu da"
//                   , @"Spanish" : @"Gaztelania"
//                   , @"Submit" : @"Bidali"
//                   , @"Success" : @"Zuzena"
//                   , @"Under the 18" : @"18 urtetik beherakoak"
//                   , @"User information stored successfully" : @"Erabiltzailearen datuak era egokian gorde dira."
//                   , @"User name:" : @"Erabiltzailearen izena"
//                   , @"Write here.." : @"Idatzi hemen..."
//                   , @"Yes" : @"Bai"
//                   , @"Your name" : @"Zure izena:"
//                   , @"Your Opinion" : @"Zure iritzia"
//                   , @"Zaldu" : @"Zaldu"
//                   , @"Zandamendi" : @"Zandamendi"
//                   , @"Zubiete" : @"Zubiete"
//                   
//                   };
    return self;
}

+(NSDictionary*)getBasque {
    static NSDictionary *inst = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        inst = @{@"18 - 35" : @"18 - 35"
                 , @"36 - 65" : @"36 - 65"
                 , @"About" : @"Zeri buruz"
                 , @"Age" : @"Adina*"
                 , @"AR Layer" : @"EA Kapa"
                 , @"Area of residence" : @"Auzoa"
                 , @"Basque" : @"Euskara"
                 , @"Billboard" : @"Etiketak"
                 , @"Cannot connect to server.\nPlease check your internet connection" : @"Ez da possible zerbitzariarekin konektatzea"
                 , @"Done" : @"Eginda"
                 , @"Downloading ..." : @"Deskargatzen..."
                 , @"Empty Fields" : @"Hutsuneak betetzeko"
                 , @"English" : @"Ingelesa"
                 , @"Error" : @"Akatza"
                 , @"Error loading questionnaire" : @"Akatsa galdeketa kargatzerakoan"
                 , @"Female" : @"Emakumezkoa"
                 , @"Gender" : @"Genero*"
                 , @"Irazagorria" : @"Iratzagorria"
                 , @"Language" : @"Hizkuntza"
                 , @"List" : @"Lista"
                 , @"Male" : @"Gizona"
                 , @"Map" : @"Mapa"
                 , @"Molinar" : @"Molinar"
                 , @"Navigation" : @"Nabigazioa"
                 //                   , @"Nearby" : @"Ingurukoa"
                 , @"Nearby" : @"Hurbiltasunaren arabera"
                 , @"No" : @"Ez"
                 , @"None" : @"Gordexolatik kanpo"
                 , @"No internet connection" : @"Internetekin konexio falta dago"
                 , @"OK" : @"Zuzena"
                 , @"Optional" : @"* Aukerakoa"
                 , @"Over the 65" : @"65 urtetik gorakoak"
                 , @"Personal Information" : @"Datu pertsonalak"
                 , @"Please fill all the questions" : @"Mesedez erantzun itzazu galdera guztiak"
                 , @"Please provide your information" : @"Mesedez bete itzazu zure datuak."
                 , @"Ponton Urarte" : @"Ponton Urarte"
                 , @"Provide Opinion" : @"Iritzia eman"
                 , @"Provide opinion" : @"Iritzia eman"
                 , @"Questionnaire is not valid" : @"Kuestionarioa ez da zuzena"
                 , @"Questionnaire updated." : @"Kuestionarioa eguneratuta"
                 , @"Refresh" : @"Freskatu"
                 , @"Resident in Gordexola" : @"Gordexolan auzokoa"
                 , @"Results" : @"Emaitzak"
                 , @"Save" : @"Gorde"
                 //                   , @"Scan" : @"Irudi bidez"
                 , @"Scan" : @"Irudiaren arabera"
                 , @"Select an answer" : @"Erantzun bat aukeratu"
                 , @"Shake to refresh position" : @"Gailua astindu kokapena eguneratzeko"
                 , @"Something went wrong while submitting the questionnaire" : @"Kuestionario hau bidaltzerakoan akatza agertu da"
                 , @"Spanish" : @"Gaztelania"
                 , @"Submit" : @"Bidali"
                 , @"Success" : @"Zuzena"
                 , @"Under the 18" : @"18 urtetik beherakoak"
                 , @"User information stored successfully" : @"Erabiltzailearen datuak era egokian gorde dira."
                 , @"User name:" : @"Erabiltzailearen izena"
                 , @"Write here.." : @"Idatzi hemen..."
                 , @"Yes" : @"Bai"
                 , @"Your name" : @"Zure izena:"
                 , @"Your Opinion" : @"Zure iritzia"
                 , @"Zaldu" : @"Zaldu"
                 , @"Zandamendi" : @"Sandamendi"
                 , @"Zubiete" : @"Zubiete"
                 
                 };
    });
    return inst;
}


+ (NSString*) getLocalizedString:(NSString*) key;
{
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
    NSLog(@"key = %@", key);
    NSLog(@"localized string = %@", NSLocalizedString(key, key));
    if(del.basque)
        return [[DataHandler getBasque] objectForKey:key];
    else
        return NSLocalizedString(key, key);
}

//- (NSString*) createAnonymousUserSynchronous
//{
//    NSString *identifier = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
//    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@/user/anonymous",SERVICE_CENTRE_URL]]];
//    [request setRequestMethod:@"POST"];
//    [request addBasicAuthenticationHeaderWithUsername:SERVICE_CENTER_USERNAME andPassword:SERVICE_CENTER_PASSWORD];
//    [request setTimeOutSeconds:10.0];
//    NSDictionary *requestInputDict = [NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:identifier, @"[2]", nil] forKeys:[NSArray arrayWithObjects:@"unique", @"customer_ids", nil]];
//    NSError *error1;
//    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:requestInputDict
//                                                       options:NSJSONWritingPrettyPrinted // Pass 0 if you don't care about the readability of the generated string
//                                                         error:&error1];
//    
//    if (! jsonData) {
//        //        NSLog(@"error generating request input");
//        return nil;
//    }
//    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//    //    NSString *postBodyStr = [NSString stringWithFormat:@"{\"unique\":\"%@\", \"customer_ids\": [2]}", identifier];
//    NSMutableData *postBodyData = [NSMutableData dataWithData:[jsonString dataUsingEncoding:NSUTF8StringEncoding]];
//    [request appendPostData:postBodyData];
//    [request addRequestHeader:@"Content-type" value:@"application/json"];
//    [request startSynchronous];
//    //    NSLog(@"user response = %@", [request responseString]);
//    if([request error])
//    {
//        return nil;
//    }
//    else
//    {
//        if([request responseStatusCode] == 200)
//        {
//            NSError *error;
//            NSDictionary * parsedData = [NSJSONSerialization JSONObjectWithData:[request responseData] options:kNilOptions error:&error];
//            if(!error)
//            {
//                if([parsedData objectForKey:@"anonymoususerid"])
//                {
//                    return [parsedData objectForKey:@"anonymoususerid"];
////                    [defaults setValue:[parsedData objectForKey:@"anonymoususerid"] forKey:DEFAULTS_ANONYMOUSUSERID];
////                    [defaults synchronize];
////                    AppDelegate *del = [UIApplication sharedApplication].delegate;
////                    del.userID = [parsedData objectForKey:@"anonymoususerid"];
////                    [self.delegate gotAnonymousUser:YES FromSettings:NO];
//                }
//                else
//                {
//                    return nil;
//                    [self.delegate gotAnonymousUser:NO FromSettings:NO];
//                    //                    NSLog(@"createAnonymousUser : cant find anonymoususerid key in server response");
//                }
//            }
//            else
//            {
//                //                NSLog(@"createAnonymousUser : response parsing error");
//                return nil;
//                [self.delegate gotAnonymousUser:NO FromSettings:NO];
//            }
//        }
//        else
//        {
//            //            NSLog(@"createAnonymousUser : response code not 200");
//            return nil;
//            [self.delegate gotAnonymousUser:NO FromSettings:NO];
//        }
//    }
//    
//}
- (void)createAnonymousUser
{
    NSString *identifier = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
//    NSLog(@"identifier = %@", identifier);
//    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"https://urbanplanning.yucat.com/servicecenter/api/user/anonymous"]];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@/user/anonymous",SERVICE_CENTRE_URL]]];
    [request setRequestMethod:@"POST"];
    [request addBasicAuthenticationHeaderWithUsername:SERVICE_CENTER_USERNAME andPassword:SERVICE_CENTER_PASSWORD];
    [request setTimeOutSeconds:10.0];
    NSDictionary *requestInputDict = [NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:identifier, @"[2]", nil] forKeys:[NSArray arrayWithObjects:@"unique", @"customer_ids", nil]];
    NSError *error1;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:requestInputDict
                                                       options:NSJSONWritingPrettyPrinted // Pass 0 if you don't care about the readability of the generated string
                                                         error:&error1];
    
    if (! jsonData) {
//        NSLog(@"error generating request input");
        return;
    }
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//    NSString *postBodyStr = [NSString stringWithFormat:@"{\"unique\":\"%@\", \"customer_ids\": [2]}", identifier];
    NSMutableData *postBodyData = [NSMutableData dataWithData:[jsonString dataUsingEncoding:NSUTF8StringEncoding]];
    [request appendPostData:postBodyData];
    [request addRequestHeader:@"Content-type" value:@"application/json"];
    [request startSynchronous];
//    NSLog(@"user response = %@", [request responseString]);
    if([request error])
    {
        NSLog(@"anonymous: error");
        if([defaults objectForKey:@"anonymoususerid"])
        {
            AppDelegate *del = [[UIApplication sharedApplication] delegate];
            del.userID = [defaults objectForKey:@"anonymoususerid"];
            [self.delegate gotAnonymousUser:YES FromSettings:YES];
        }
        else{
            [self.delegate gotAnonymousUser:NO FromSettings:YES];
        }
        return;
    }
    else
    {
        if([request responseStatusCode] == 200)
        {
            NSError *error;
            NSDictionary * parsedData = [NSJSONSerialization JSONObjectWithData:[request responseData] options:kNilOptions error:&error];
            if(!error)
            {
                if([parsedData objectForKey:@"anonymoususerid"])
                {
                    [defaults setValue:[parsedData objectForKey:@"anonymoususerid"] forKey:DEFAULTS_ANONYMOUSUSERID];
                    [defaults synchronize];
                    AppDelegate *del = [UIApplication sharedApplication].delegate;
                    del.userID = (NSNumber*) [parsedData objectForKey:@"anonymoususerid"];
                    [self.delegate gotAnonymousUser:YES FromSettings:NO];
                }
                else
                {
                    [self.delegate gotAnonymousUser:NO FromSettings:NO];
//                    NSLog(@"createAnonymousUser : cant find anonymoususerid key in server response");
                }
            }
            else
            {
//                NSLog(@"createAnonymousUser : response parsing error");
                [self.delegate gotAnonymousUser:NO FromSettings:NO];
            }
        }
        else
        {
//            NSLog(@"createAnonymousUser : response code not 200");
            [self.delegate gotAnonymousUser:NO FromSettings:NO];
        }
    }
}



- (void)getPermissions
{
//    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"https://https://urbanplanning.yucat.com/servicecenter/api/account/permission"]];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@/account/permission",SERVICE_CENTRE_URL]]];
    [request setRequestMethod:@"GET"];
    [request addBasicAuthenticationHeaderWithUsername:SERVICE_CENTER_USERNAME andPassword:SERVICE_CENTER_PASSWORD];
    [request setTimeOutSeconds:10.0];
    [request startSynchronous];
//    NSLog(@"permissions response %@", [request responseString]);
    if([request error])
    {
    }
    else
    {
        if([request responseStatusCode] == 200)
        {
            NSError *error;
            NSArray *parsedData = [NSJSONSerialization JSONObjectWithData:[request responseData] options:kNilOptions error:&error];
            AppDelegate *del = [UIApplication sharedApplication].delegate;
            del.permissions = parsedData;
            if(!error)
            {
                AppDelegate *del = [UIApplication sharedApplication].delegate;
                del.permissions = parsedData;
                [self.delegate gotPermissions:YES FromSettings:NO];
            }
        }
    }
}
/* 0 : lang
   1 : entity id
 */

- (BOOL) getUserQuestionnaire: (BOOL) update{
//    NSString *anonymousID = [defaults objectForKey:DEFAULTS_ANONYMOUSUSERID];
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
    NSNumber *anonymousID = del.userID;
    if(!anonymousID)
        return NO;
    NSString *name = [defaults objectForKey:DEFAULTS_NAME];
    NSNumber *v1 = [defaults objectForKey:DEFAULTS_VALUE1];
    NSNumber *v2 = [defaults objectForKey:DEFAULTS_VALUE2];
//    NSNumber *v3 = [defaults objectForKey:DEFAULTS_VALUE3];
    NSNumber *v4 = [defaults objectForKey:DEFAULTS_VALUE4];
    NSNumber *v5 = [defaults objectForKey:DEFAULTS_VALUE5];
    if(!v1 || !v2 || !v4 || !v5)
        return NO;
    NSString *urlString = [NSString stringWithFormat:@"%@/questionnaire/%@/USER-XX/USER-XX", URBAN_PLANNING_API_URL, [anonymousID stringValue]];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:urlString]];
    [request setRequestMethod:@"GET"];
    [request addBasicAuthenticationHeaderWithUsername:SERVICE_CENTER_USERNAME andPassword:SERVICE_CENTER_PASSWORD];
    [request setTimeOutSeconds:10.0];
    [request startSynchronous];
    if([request error]){
        //Error
        return NO;
    }
    else{
        if([request responseStatusCode] == 200){
            NSError *error;
            NSLog(@"response string = %@", [request responseString]);
            //send questionnaire
            NSMutableDictionary *parsedData = [NSJSONSerialization JSONObjectWithData:[request responseData] options:NSJSONReadingMutableContainers error:&error];
            if(!error)
            {
                //should go here
                NSArray *questions = (NSArray*) [[[[parsedData objectForKey:@"questionnaire"] objectForKey:@"categories"] objectAtIndex:0] objectForKey:@"questions"];
                //reset the previous questions
                NSArray *answerOptions;
                for(int i = 1; i < 4; i++){
                    answerOptions = [[questions objectAtIndex:i] objectForKey:@"answeroptions"];
                    for(NSDictionary *dict in answerOptions){
                        [dict setValue:[NSNumber numberWithBool:NO] forKey:@"selected"];
                    }
                }
                [[questions objectAtIndex:0] setValue:name forKey:@"comment" ];
                if([v1 integerValue] != -1)
                    [[[[questions objectAtIndex:1] objectForKey:@"answeroptions"] objectAtIndex:[v1 integerValue]] setValue:[NSNumber numberWithBool:YES] forKey:@"selected"];
                if([v2 integerValue] != -1)
                    [[[[questions objectAtIndex:2] objectForKey:@"answeroptions"] objectAtIndex:[v2 integerValue]] setValue:[NSNumber numberWithBool:YES] forKey:@"selected"];
                [[[[questions objectAtIndex:3] objectForKey:@"answeroptions"] objectAtIndex:[v4 integerValue]] setValue:[NSNumber numberWithBool:YES] forKey:@"selected"];
                return [self submitUserQuestionnaire:parsedData];
//                [[[[questions objectAtIndex:4] objectForKey:@"answeroptions"] objectAtIndex:v1] setValue:[NSNumber numberWithBool:YES] forKey:@"selected"];
//                [[[[questions objectAtIndex:1] objectForKey:@"answeroptions"] objectAtIndex:v1] setValue:[NSNumber numberWithBool:YES] forKey:@"selected"];
            }
            else
                //parsing error
                return NO;
        }
        else
        {
            return NO;
        }
    }
}
- (NSMutableDictionary*) getQuestionaireWithOptions: (NSArray*) options
{
    NSString *lang = @"XX";
//    NSString *lang = [options objectAtIndex:0];
    NSString *entityId = [options objectAtIndex:1];
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
    NSNumber *anonymousid = del.userID;
    if(!anonymousid)
    {
        if([self.delegate respondsToSelector:@selector(gotQuestionnaire:)])
            [self.delegate gotQuestionnaire:nil];
        return nil;
    }
//    NSString *urlString = [NSString stringWithFormat:@"https://urbanplanning-test.yucat.com/dialogandvisualization/api/questionnaire/%@/UP-%@-%@/%@",anonymousid,entityId, lang, entityId];
    NSString *urlString = [NSString stringWithFormat:@"%@/questionnaire/%@/UP-%@-%@/%@",URBAN_PLANNING_API_URL,[anonymousid stringValue],entityId,lang,entityId];
//    NSString *urlString = [NSString stringWithFormat:@"https://urbanplanning-test.yucat.com/dialogandvisualization/api/questionnaire/1/UP-36-EN/36"];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:urlString]];
    [request setRequestMethod:@"GET"];
    [request addBasicAuthenticationHeaderWithUsername:SERVICE_CENTER_USERNAME andPassword:SERVICE_CENTER_PASSWORD];
    [request setTimeOutSeconds:10.0];
    [request startSynchronous];
    NSLog(@"questionnaire response = %@", [request responseString]);
    if([request error])
    {
        if([self.delegate respondsToSelector:@selector(gotQuestionnaire:)])
            [self.delegate gotQuestionnaire:nil];
        return nil;
    }
    else
    {
        if([request responseStatusCode] == 200)
        {
            NSError *error;
            NSMutableDictionary *parsedData = [NSJSONSerialization JSONObjectWithData:[request responseData] options:NSJSONReadingMutableContainers error:&error];
            if(!error)
            {
                if([self.delegate respondsToSelector:@selector(gotQuestionnaire:)])
                    [self.delegate gotQuestionnaire:parsedData];
                return parsedData;
            }
            if([self.delegate respondsToSelector:@selector(gotQuestionnaire:)])
                [self.delegate gotQuestionnaire:nil];
            return nil;
        }
    }
    if([self.delegate respondsToSelector:@selector(gotQuestionnaire:)])
        [self.delegate gotQuestionnaire:nil];
    return nil;
}
- (void)getAREntities
{
    NSLog(@"requested AR");
    ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:[NSURL URLWithString:@"http://augreal.mklab.iti.gr/api_v3/ar_get.php"]];
    [request setRequestMethod:@"POST"];
    [request addBasicAuthenticationHeaderWithUsername:@"ar_api_user" andPassword:@"certh"];
    [request addPostValue:@"-89.1" forKey:@"x0down"];
    [request addPostValue:@"89.3" forKey:@"x0up"];
    [request addPostValue:@"0.1" forKey:@"y0down"];
    [request addPostValue:@"170.5" forKey:@"y0up"];
    [request addPostValue:@"2" forKey:@"id_app"];
    [request addPostValue:@"ar_api_user" forKey:@"username"];
    [request addPostValue:@"certh" forKey:@"password"];
    [request setTimeOutSeconds:10.0];
    [request startSynchronous];
    if([request error])
    {
        [self.delegate gotAREntities:NO FromSettings:YES];
    }
    else
    {
        NSError *error;
        NSArray *parsedData = [NSJSONSerialization JSONObjectWithData:[request responseData] options:kNilOptions error:&error];
        AppDelegate *del = [UIApplication sharedApplication].delegate;
        [del.entityData setTheEntitiesFromArr:parsedData];
        [self.delegate gotAREntities:YES FromSettings:NO];
//        NSLog(@"entities count = %d", [parsedData count]);
//        NSLog(@"ar response : %@", [request responseString]);
        if(!error)
        {
        }
    }
        NSLog(@"finished AR");
}

- (NSString*)downloadAndGetIBSTrackingPath
{
    NSLog(@"downloadAndGetIBSTrackingPath");
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"http://augreal.mklab.iti.gr/api_Metaio_v2/IBS/index.php"]];
    [request setTimeOutSeconds:10.0];
    [request startSynchronous];
    if(![request error])
    {
        
        NSArray *paths = NSSearchPathForDirectoriesInDomains
        (NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        NSString *fileName = [NSString stringWithFormat:@"%@/tracking.zip",
                              documentsDirectory];
        request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"http://augreal.mklab.iti.gr/api_Metaio_v2/IBS/tracking.zip"]];
        [request setDownloadDestinationPath:fileName];
        [request setTimeOutSeconds:10.0];
        [request startSynchronous];
        if(![request error])
        {
//            NSLog(@"filename = %@", fileName);
            return fileName;
        }
        else
        {
            return nil;
        }
    }
    else
    {
        return nil;
    }
}

- (void) saveIBSHash
{
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"http://augreal.mklab.iti.gr/api_v3/provideFileHashes.php?id=2.tracking"]];
    [request setTimeOutSeconds:3.0];
    [request setRequestMethod:@"GET"];
    [request startSynchronous];
    if(![request error]){
        if([request responseString]){
            NSLog(@"saving fresh hash = %@", [request responseString]);
            [defaults setObject:[request responseString] forKey:@"2.tracking"];
            [defaults synchronize];
        }
        else{
            NSLog(@"error trying to save ibs hash string");
        }
    }
    else
        NSLog(@"error trying to save ibs hash string");
}

- (void) saveHashForModelWithFilename: (NSString*) hashFileName
{
    NSLog(@"trying to save fresh hashfilename = %@", hashFileName);
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://augreal.mklab.iti.gr/api_v3/provideFileHashes.php?filename=%@", hashFileName]]];
    [request setTimeOutSeconds:3.0];
    [request setRequestMethod:@"GET"];
    [request startSynchronous];
    if(![request error])
    {
        if([request responseString]){
            NSLog(@"saving fresh hash = %@", [request responseString]);
            [[NSUserDefaults standardUserDefaults] setObject:[request responseString] forKey:hashFileName];
            NSLog(@"setting object : %@", [request responseString]);
            NSLog(@"for key : %@", hashFileName);
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
        else
            NSLog(@"error trying to save fresh hash string on defaults");
    }
    else
        NSLog(@"error trying to save fresh hash string on defaults");
}

//?id=2.tracking
- (NSString*) getIBS{
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"http://augreal.mklab.iti.gr/api_Metaio_v2/IBS/indexjson.php"]];
    [request setTimeOutSeconds:10.0];
    [request startSynchronous];
    if(![request error])
    {
        NSError *error;
        NSLog(@"trying to parse json");
        NSArray *objects = [NSJSONSerialization JSONObjectWithData:[request responseData] options:0 error:&error];
        if(!error)
        {
            NSLog(@"parsed json success");
            AppDelegate *del = [[UIApplication sharedApplication] delegate];
            for(NSDictionary *object in objects){
                NSString *objID = [object objectForKey:@"id"];
                NSLog(@"objID = %@", objID);
                CGFloat scale = [[object objectForKey:@"trackImScale"] floatValue];
                NSLog(@"scale = %f", scale);
                CGFloat rotation = [[object objectForKey:@"trackImRot"] floatValue];
                NSLog(@"rotation = %f", rotation);
                AREntity *entity = [del.entityData getEntityByID:objID];
                entity.scale = scale;
                entity.rotation = rotation;
                entity.shouldTrack = YES;
            }
            if([self shouldDownloadTrackingZip]){
                NSLog(@"should download tracking zip");
                NSString *returnValue = [self downloadAndGetIBSTrackingPath];
                NSLog(@"saving ibs hash");
                [self saveIBSHash];
//                return [self downloadAndGetIBSTrackingPath];
                NSLog(@"returning returnValue = %@", returnValue);
                return returnValue;
            }
            else{
                NSLog(@"should not download tracking zip");
                NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
                NSString *documentsDirectory = [paths objectAtIndex:0];
                NSString *fileName = [NSString stringWithFormat:@"%@/tracking.zip", documentsDirectory];
                NSLog(@"returning filename = %@", fileName);
                return fileName;
            }
        }
        else{
            NSLog(@"getIBS : error parsing json");
        }
    }
    else{
        NSLog(@"getIBS : error connecting");
        return nil;
    }
}


- (BOOL) shouldDownloadTrackingZip{
    NSString *trackingID = @"2.tracking";
    NSString *localHash = [[NSUserDefaults standardUserDefaults] objectForKey:trackingID];
    if(!localHash){
        NSLog(@"there is no local hash");
        return YES; //there is no local hash
    }
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://augreal.mklab.iti.gr/api_v3/provideFileHashes.php?id=%@", trackingID]]];
    [request setTimeOutSeconds:3.0];
    [request setRequestMethod:@"GET"];
    [request startSynchronous];
    if(![request error])
        if([request responseString]){
            NSLog(@"responseString = %@", [request responseString]);
            if([localHash isEqualToString:[request responseString]])
            {
                NSLog(@"hash matcheS");
                return NO; // hash matches
            }
            else{
                [[NSUserDefaults standardUserDefaults] setObject:[request responseString] forKey:trackingID];
                NSLog(@"setting object : %@", [request responseString]);
                NSLog(@"hash does not match");
                return YES; // hash does not match
            }
        }
        else{
            NSLog(@"server doesnt return anything");
            return YES; // server doesnt return anything
        }
        else{
            NSLog(@"server connection error");
            return YES; // connection error
        }
}
- (void)getModel:(AREntity *)entity Number:(NSInteger)modelNo
{
    NSString *path = [entity get3dPathForModel:modelNo];
    [self.delegate gotModelForEntity:entity withPath:path];
}

- (BOOL)validateQuestionnaire:(NSDictionary *)questionnaire
{
    NSArray *questions = [[[[questionnaire objectForKey:@"questionnaire"] objectForKey:@"categories"] objectAtIndex:0] objectForKey:@"questions"];
    NSInteger totalSelectedCount = 0;
    for(NSDictionary *question in questions)
    {
        if([[question objectForKey:@"displaytype"] integerValue] == 0)
        {
            NSArray *answeroptions = [question objectForKey:@"answeroptions"];
            NSInteger selectedCount = 0;
            for(NSDictionary *answeroption in answeroptions)
            {
                if([[answeroption objectForKey:@"selected"] boolValue])
                {
                    selectedCount++;
                    totalSelectedCount++;
                }
            }
            if(selectedCount > 1){
                return NO;
            }
        }
        else if([[question objectForKey:@"displaytype"] integerValue] == 2)
        {
            //can be empty
//            NSString *comment = [question objectForKey:@"comment"];
//            if([comment isEqual:[NSNull null]])
//            {
//                return NO;
//            }
        }
    }
    if(totalSelectedCount==0) return NO;
    return YES;
}
- (BOOL) submitUserQuestionnaire: (NSDictionary*) questionnaire
{
//    NSString *anonymousUserId = [defaults objectForKey:DEFAULTS_ANONYMOUSUSERID];
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
    NSNumber *anonymousUserId = del.userID;
    if(!anonymousUserId)
        return NO;
    NSError *error;
//    NSMutableDictionary *questionnaireToSend = [@{@"categories" : [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"categories"],
//                                          @"code" : @"USER-XX",
//                                          @"objectcode" : @"USER-XX",
//                                          @"id" : anonymousUserId
//                                          
//                                          } mutableCopy];
    NSMutableDictionary *questionnaireToSend = [@{@"id" : [NSNumber numberWithInt:10],
                                                  @"objectcode" : @"USER-XX",
                                                  @"code" : @"USER-XX",
                                                  @"categories" : [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"categories"]} mutableCopy];
    [[[questionnaireToSend objectForKey:@"categories"] objectAtIndex:0] removeObjectForKey:@"description"];
    NSArray *questions = [[[questionnaireToSend objectForKey:@"categories"] objectAtIndex:0] objectForKey:@"questions"];
    for(NSMutableDictionary *question in questions){
        NSArray *keys = [question allKeysForObject:[NSNull null]];
        [question removeObjectsForKeys:keys];
    }
    NSArray *keys = [questionnaireToSend allKeysForObject:[NSNull null]];
    [questionnaireToSend removeObjectsForKeys:keys];
//    [[[mutQuestionnaireToSend objectForKey:@"categories"] objectAtIndex:0] removeObjectForKey:@"description"];
//    [[[mutQuestionnaireToSend objectForKey:@"categories"] objectAtIndex:0] obj]
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:questionnaireToSend options:NSJSONWritingPrettyPrinted error:&error];
    if(! jsonData) {
        return NO;
    } else {
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        NSLog(@"json = %@", jsonString);
        NSMutableData *postBodyData = [NSMutableData dataWithData:[jsonString dataUsingEncoding:NSUTF8StringEncoding]];
        NSString *urlString = [NSString stringWithFormat:@"%@/questionnaire/%@", URBAN_PLANNING_API_URL, [anonymousUserId stringValue]];
        ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:urlString]];
        [request setRequestMethod:@"POST"];
        [request addBasicAuthenticationHeaderWithUsername:SERVICE_CENTER_USERNAME andPassword:SERVICE_CENTER_PASSWORD];
        [request appendPostData:postBodyData];
        [request setTimeOutSeconds:10.0];
        [request addRequestHeader:@"Content-type" value:@"application/json"];
        [request addRequestHeader:@"Accept" value:@"application/json"];
        [request startSynchronous];
        if([request error])
        {
            return NO;
        }
        else{
            NSLog(@"response string = %@",[request responseString]);
            if([request responseStatusCode] == 200)
            {
                [defaults setObject:[NSNumber numberWithBool:YES] forKey:DEFAULTS_QUESTIONNAIRE_SENT];
                [defaults synchronize];
                return YES;
            }
            else{
                return NO;
            }
        }
    }
}
- (BOOL)submitQuestionnaire:(NSDictionary *)questionnaire WithLat: (CLLocationDegrees) latitude AndLon: (CLLocationDegrees) longitude
{
    NSString *anonymoususerid = [defaults objectForKey:DEFAULTS_ANONYMOUSUSERID];
    NSLog(@"anonymoususerid = %@", anonymoususerid);
    if(!anonymoususerid)
    {
//        NSLog(@"anonymoususerid not exists");
        return NO;
    }
    NSString *gender = [defaults objectForKey:DEFAULTS_SEX];
    NSString *useragerange = [defaults objectForKey:DEFAULTS_AGE];
    NSString *code = [[questionnaire objectForKey:@"questionnaire" ] objectForKey:@"code"];
    NSString *description = [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"description"];
    NSString *objectCode = [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"objectcode"];
    NSNumber *_id = [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"id"];
    NSArray *categories = [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"categories"];
    NSDictionary *dictToSend;
    if(!gender)
        gender = @"";
    if(!useragerange)
        useragerange = @"";
    if(latitude == 0.0 && longitude == 0.0){
        dictToSend = [NSDictionary dictionaryWithObjects:@[gender, useragerange, categories,code,description,objectCode, _id] forKeys:@[@"gender", @"useragerange", @"categories", @"code", @"description", @"objectcode",@"id"]];
    }
    else{
        dictToSend = [NSDictionary dictionaryWithObjects:@[gender, useragerange, categories,code,description,objectCode, _id, [NSNumber numberWithDouble:latitude], [NSNumber numberWithDouble:longitude]] forKeys:@[@"gender", @"useragerange", @"categories", @"code", @"description", @"objectcode",@"id", @"lat", @"lng"]];
    }
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictToSend
                                                       options:NSJSONWritingPrettyPrinted // Pass 0 if you don't care about the readability of the generated string
                                                         error:&error];
    NSLog(@"test = %@", [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding]);
    
    if (! jsonData) {
//        NSLog(@"submitQuestionnaire: json error: %@", [error localizedDescription]);
        NSLog(@"json data is nil");
        return NO;
    } else {
        NSLog(@"questionnaire = %@", [questionnaire description]);
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//        NSLog(@"jsonString = %@", jsonString);
//        NSString *urlString = [NSString stringWithFormat:@"https://urbanplanning-test.yucat.com/dialogandvisualization/api/questionnaire/%@", anonymoususerid];
        NSString *urlString = [NSString stringWithFormat:@"%@/questionnaire/%@",URBAN_PLANNING_API_URL, anonymoususerid];
//        ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"https://urbanplanning-test.yucat.com/dialogandvisualization/api/questionnaire/1"]];
            NSMutableData *postBodyData = [NSMutableData dataWithData:[jsonString dataUsingEncoding:NSUTF8StringEncoding]];
        ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:urlString]];
        [request setRequestMethod:@"POST"];
        [request addBasicAuthenticationHeaderWithUsername:SERVICE_CENTER_USERNAME andPassword:SERVICE_CENTER_PASSWORD];
        [request appendPostData:postBodyData];
        [request setTimeOutSeconds:10.0];
        [request addRequestHeader:@"Content-type" value:@"application/json"];
        [request startSynchronous];
//        NSLog(@"submit questionnaire response = %@", [request responseString]);
//        NSLog(@"submit questionnaire status code = %d", [request responseStatusCode]);
        if([request error])
        {
            return NO;
            NSLog(@"request error");
        }
        else
        {
            if([request responseStatusCode] == 200)
            {
                return YES;
                NSLog(@"request komple");
            }
            else
            {
                NSLog(@"request status code = %d (error)", [request responseStatusCode]);
                return NO;
            }
        }
    }
}

- (void) setUserInformation:(NSString *)name Age:(NSString *)age Sex:(NSString *)sex ResidenceZone:(NSString *)residenceZone
{
    [defaults setValue:name forKey:DEFAULTS_NAME];
    [defaults setValue:age forKey:DEFAULTS_AGE];
    [defaults setValue:sex forKey:DEFAULTS_SEX];
    [defaults setValue:residenceZone forKey:DEFAULTS_RESIDENCE_ZONE];
    [defaults synchronize];
}

- (void)setUserInformation:(NSString *)name Age:(NSString *)age Sex:(NSString *)sex ResidenceZone:(NSString *)residenceZone Language:(NSString *)language
{
    [defaults setValue:name forKey:DEFAULTS_NAME];
    [defaults setValue:age forKey:DEFAULTS_AGE];
    [defaults setValue:sex forKey:DEFAULTS_SEX];
    [defaults setValue:residenceZone forKey:DEFAULTS_RESIDENCE_ZONE];
    
    NSArray *preferredLanguages = [NSLocale preferredLanguages];
    NSInteger espanolIndex = [preferredLanguages indexOfObject:@"es"];
    NSInteger englishIndex = [preferredLanguages indexOfObject:@"en"];
    NSString *languageKey;
    if(espanolIndex < englishIndex)
        languageKey = @"Spanish";
    else
        languageKey = @"English";
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
//    if([language isEqualToString:NSLocalizedString(@"English", @"English")]){
//        [defaults setObject:@"N" forKey:@"basque"];
//        del.basque = NO;
//    }
//    else if([language isEqualToString:NSLocalizedString(@"Spanish", @"Spanish")]){
//        del.basque = NO;
//        [defaults setObject:@"N" forKey:@"basque"];
//    }
//    else{
//        del.basque = YES;
//        [defaults setObject:@"Y" forKey:@"basque"];
//    }
    NSLog(@"language = %@", language);
    NSLog(@"localized = %@", [DataHandler getLocalizedString:languageKey]);
    if([language isEqualToString:[DataHandler getLocalizedString:languageKey]]){
        [defaults setObject:@"N" forKey:@"basque"];
        del.basque = NO;
    }
//    else if([language isEqualToString:[self getLocalizedString:@"Spanish"]]){
//        [defaults setObject:@"N" forKey:@"basque"];
//        del.basque = NO;
//    }
    else{
        [defaults setObject:@"Y" forKey:@"basque"];
        del.basque = YES;
    }
    [defaults setValue:language forKeyPath:DEFAULTS_LANGUAGE];
    [defaults synchronize];
}

- (void)storeUserv1:(NSInteger)value1 v2:(NSInteger)value2 v4:(NSInteger)value4 v5:(NSInteger)value5
{
    [defaults setValue:[NSNumber numberWithInt:value1] forKey:DEFAULTS_VALUE1];
    [defaults setValue:[NSNumber numberWithInt:value2] forKey:DEFAULTS_VALUE2];
    [defaults setValue:[NSNumber numberWithInt:value4] forKey:DEFAULTS_VALUE4];
    [defaults setValue:[NSNumber numberWithInt:value5] forKey:DEFAULTS_VALUE5];
    [defaults synchronize];
}
@end
