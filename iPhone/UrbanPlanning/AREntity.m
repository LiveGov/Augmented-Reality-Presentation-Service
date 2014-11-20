//
//  AREntity.m
//  Urban Planning
//
//  Created by George Liaros on 2/25/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import "AREntity.h"
#import "AppDelegate.h"

@implementation AREntity
@synthesize title_en,title_es,description_en,description_es,identifier,latitude,longitude,altitude;

+ (AREntity *)fromDict:(NSDictionary *)dictionary
{
    AREntity *entity = [[AREntity alloc] init];
    entity.title_en = [dictionary objectForKey:@"title"];
//    NSLog(@"%@", entity.title_en);
    entity.title_es = [dictionary objectForKey:@"titleB"];
    entity.title_eu = [dictionary objectForKey:@"titleC"];
    entity.description_en = [dictionary objectForKey:@"description"];
    entity.description_es = [dictionary objectForKey:@"descriptionB"];
    entity.description_eu = [dictionary objectForKey:@"descriptionC"];
    entity.identifier = [dictionary objectForKey:@"id"];
    entity.latitude = [[dictionary objectForKey:@"latitude"] floatValue];
    entity.longitude = [[dictionary objectForKey:@"longitude"] floatValue];
    entity.altitude = [[dictionary objectForKey:@"altitude"] floatValue];
    entity.numModels = [[dictionary objectForKey:@"models"] integerValue];
    entity.shouldTrack = NO;
    [entity getImage];
//    entity.dImage = [UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://augreal.mklab.iti.gr/Models3D_DB/%@/1/AR_%@_1.jpg",entity.identifier,entity.identifier]]]];
//    NSLog(@"komple");
    return entity;
}

-(NSString*) getLocalizedTitle
{
//    if([[[NSLocale preferredLanguages] objectAtIndex:0] isEqualToString:@"es"])
//        return title_es;
//    else
//        return title_en;
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
    if(del.basque)
        return self.title_eu;
    else
        return self.title_es;
}

-(NSString*) getLocalizedDescription
{
//    if([[[NSLocale preferredLanguages] objectAtIndex:0] isEqualToString:@"es"])
//        return description_es;
//    else
//        return description_en;
    NSArray *preferredLanguages = [NSLocale preferredLanguages];
    NSInteger espanolIndex = [preferredLanguages indexOfObject:@"es"];
    NSInteger englishIndex = [preferredLanguages indexOfObject:@"en"];
    
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
    if(del.basque)
        return self.description_eu;
    else{
        if(espanolIndex < englishIndex)
            return self.description_es;
        else
            return self.description_en;
    }
}

-(void) getImage
{
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://augreal.mklab.iti.gr/Models3D_DB/%@/1/AR_%@_1.jpg",self.identifier,self.identifier]]];
    [request setTimeOutSeconds:3.0];
    [request startSynchronous];
    if(![request error])
        self.dImage = [UIImage imageWithData:[request responseData]];
    else
        self.dImage = [[UIImage alloc] init];
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

- (BOOL) needToDownloadModelWithFileName: (NSString *) filename
{
    NSLog(@"needToDownloadModelWithFilename  filename = %@", filename);
    NSString *localHash = [[NSUserDefaults standardUserDefaults] objectForKey:filename];
    NSLog(@"for key : %@", filename);
    if(!localHash){
        NSLog(@"there is no local hash");
        return YES; //there is no local hash
    }
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://augreal.mklab.iti.gr/api_v3/provideFileHashes.php?filename=%@", filename]]];
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
                [[NSUserDefaults standardUserDefaults] setObject:[request responseString] forKey:filename];
                NSLog(@"setting object : %@", [request responseString]);
                NSLog(@"for key : %@", filename);
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

- (BOOL) fileExists : (NSString*) filename modelIdx: (NSInteger) index
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains
    (NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *fileName = [NSString stringWithFormat:@"%@/AR_%@_%djunaio.zip",
                          documentsDirectory,self.identifier, index];
    NSString *hashFilename = [NSString stringWithFormat:@"AR_%@_%djunaio.zip", self.identifier, index];
    BOOL fileExists = [[NSFileManager defaultManager] fileExistsAtPath:fileName];
    if(fileExists)
    {
        if([self needToDownloadModelWithFileName:hashFilename]){
            NSLog(@"file exists but hash doesnt match");
            return NO; // file exists but hash doesn't match
        }
        else{
            NSLog(@"file exists and hash matcheS");
            return YES; // file exists and hash matches
        }
    }
    else
    {
        NSLog(@"file doesnt exist");
        return NO; // file doesnt exist
    }
}



- (NSString *) get3dPathForModel:(NSInteger)modelIndex
{
    NSLog(@"requesting %d", modelIndex);
    NSLog(@"requesting %@", self.identifier);
    NSString *hashFilename = [NSString stringWithFormat:@"AR_%@_%djunaio.zip", self.identifier, modelIndex];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *fileName = [NSString stringWithFormat:@"%@/AR_%@_%djunaio.zip", documentsDirectory, self.identifier, modelIndex];
    if([self fileExists:hashFilename modelIdx:modelIndex])
    {
        return fileName; //file exists and is synchronized!
    }
    else{
        //file doesnt exist
        NSString *urlString = [NSString stringWithFormat:@"http://augreal.mklab.iti.gr/Models3D_DB/%@/%d/AR_%@_%djunaio.zip",self.identifier, modelIndex,self.identifier, modelIndex];
        NSURL *url = [NSURL URLWithString:urlString];
        ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
        [request setDownloadDestinationPath:fileName];
        [request setTimeOutSeconds:5.0];
        [request startSynchronous];
        if([request error])
        {
            NSLog(@"request error");
            return nil; // return nil - handle appropriately
        }
        else{
            NSLog(@"model downloaded successfully");
            [self saveHashForModelWithFilename:hashFilename];
            return fileName; //return freshly downloaded model
        }
    }
}

//-(NSString *)get3dPathForModel : (NSInteger) modelIndex
//{
//    NSLog(@"requesting %d", modelIndex);
//    NSLog(@"requesting %@", self.identifier);
////    AppDelegate *del = [UIApplication sharedApplication].delegate;
////    NSMutableDictionary *models = del.modelsPaths;
////    NSMutableDictionary *modelsForEntity = [models objectForKey:self.identifier];
//    NSString *hashFilename = [NSString stringWithFormat:@"AR_%@_%djunaio.zip", self.identifier, modelIndex];
//    if([self fileExists:hashFilename modelIdx:modelIndex])
//    {
//        NSString *modelsPath = [modelsForEntity objectForKey:[NSString stringWithFormat:@"%d", modelIndex]];
//        if(modelsPath){
//            NSLog(@"already downloaded");
//            return modelsPath;
//        }
//        else
//        {
//            //got the entity first model but not the specific model
//            NSArray *paths = NSSearchPathForDirectoriesInDomains
//            (NSDocumentDirectory, NSUserDomainMask, YES);
//            NSString *documentsDirectory = [paths objectAtIndex:0];
//            NSString *fileName = [NSString stringWithFormat:@"%@/AR_%@_%d.zip",
//                                  documentsDirectory,self.identifier, modelIndex];
//            NSString *urlString = [NSString stringWithFormat:@"http://augreal.mklab.iti.gr/Models3D_DB/%@/%d/AR_%@_%djunaio.zip",self.identifier, modelIndex,self.identifier, modelIndex];
//            //    NSLog(@"get 3d urlString = %@", urlString);
//            NSURL *url = [NSURL URLWithString:urlString];
//            ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
//            [request setDownloadDestinationPath:fileName];
//            [request setTimeOutSeconds:5.0];
//            [request startSynchronous];
//            if([request error])
//            {
//                NSLog(@"request error");
//                //connection error - will return old model if existing.
//            }
//            BOOL fileExists = [[NSFileManager defaultManager] fileExistsAtPath:fileName];
//            if(fileExists)
//            {
//                [modelsForEntity setObject:fileName forKey:[NSString stringWithFormat:@"%d",modelIndex]];
//                return fileName;
//            }
//            else
//            {
//                return nil;
//            }
//        }
//    }
//    else
//    {
//        //dont have the entity
//        modelsForEntity = [[NSMutableDictionary alloc] init];
//        NSArray *paths = NSSearchPathForDirectoriesInDomains
//        (NSDocumentDirectory, NSUserDomainMask, YES);
//        NSString *documentsDirectory = [paths objectAtIndex:0];
//        NSString *fileName = [NSString stringWithFormat:@"%@/AR_%@_%d.zip",
//                              documentsDirectory,self.identifier, modelIndex];
//        NSString *urlString = [NSString stringWithFormat:@"http://augreal.mklab.iti.gr/Models3D_DB/%@/%d/AR_%@_%djunaio.zip",self.identifier, modelIndex,self.identifier, modelIndex];
//        //    NSLog(@"get 3d urlString = %@", urlString);
//        NSURL *url = [NSURL URLWithString:urlString];
//        ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
//        [request setDownloadDestinationPath:fileName];
//        [request setTimeOutSeconds:5.0];
//        [request startSynchronous];
//        if([request error])
//        {
//            NSLog(@"request error");
//            //connection error - will return old model if existing.
//        }
//        BOOL fileExists = [[NSFileManager defaultManager] fileExistsAtPath:fileName];
//        if(fileExists)
//        {
//            
//            [modelsForEntity setObject:fileName forKey:[NSString stringWithFormat:@"%d",modelIndex]];
//            [models setObject:modelsForEntity forKey:self.identifier];
//            return fileName;
//        }
//        else
//        {
//            return nil;
//        }
//    }
//}


@end
